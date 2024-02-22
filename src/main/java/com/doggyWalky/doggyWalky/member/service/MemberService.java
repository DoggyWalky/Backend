package com.doggyWalky.doggyWalky.member.service;

import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.file.common.BasicImage;
import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.request.FileRequestDto;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import com.doggyWalky.doggyWalky.file.service.FileService;
import com.doggyWalky.doggyWalky.member.dto.request.MemberPatchProfileDto;
import com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.entity.MemberProfileInfo;
import com.doggyWalky.doggyWalky.member.entity.MemberSecretInfo;
import com.doggyWalky.doggyWalky.member.repository.MemberProfileInfoRepository;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import com.doggyWalky.doggyWalky.member.repository.MemberSecretInfoRepository;
import com.doggyWalky.doggyWalky.security.jwt.HmacAndBase64;
import com.doggyWalky.doggyWalky.security.redis.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberProfileInfoRepository memberProfileInfoRepository;

    private final MemberRepository memberRepository;

    private final MemberSecretInfoRepository memberSecretInfoRepository;

    private final FileService fileService;

    private final RedisService redisService;

    private final HmacAndBase64 hmacAndBase64;

    /**
     * Member pk로 회원 프로필 조회하기
     */
    @Transactional(readOnly = true)
    public List<MemberProfileResponseDto> getMemberProfiles(Long memberId) {
        List<MemberProfileResponseDto> memberProfiles = memberProfileInfoRepository.findMemberProfiles(memberId, false);
        return memberProfiles;
    }


    /**
     * 회원 프로필 수정
     */
    public void updateMemberProfile(Long memberId, MemberPatchProfileDto dto) {
        MemberProfileInfo profile = memberProfileInfoRepository.findByMemberId(memberId,false).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        System.out.println("MemberPatchProfileDto :" + dto);
        // 프로필 이미지 - fileId 값이 있는 경우
        if (dto.getFileId() != null) {

            // 회원 이미지 세팅
            File file = fileService.findFile(dto.getFileId());
            fileService.checkFileExtForProfile(file.getFileName());

            // 기본이미지 아닐 경우 기존 파일 정보 삭제
            if (!profile.getProfileImage().equals(BasicImage.BASIC_USER_IMAGE.getPath())) {
                FileRequestDto profileImage = FileRequestDto.create(TableName.MEMBER_PROFILE_INFO, profile.getId());
                FileResponseDto findProfileFile = fileService.findFilesByTableInfo(profileImage, false).get(0);
                fileService.delete(findProfileFile.getFileId());
            }

            // 공통 : 파일 정보 저장
            fileService.saveFileInfo(new FileInfo(TableName.MEMBER_PROFILE_INFO, profile.getId(), file));
            dto.setProfileImage(file.getPath());

        } else if (!StringUtils.hasText(dto.getProfileImage())) {
            // 사진을 제거한 상태이기 때문에 기본 이미지로 세팅해주는 작업
            // 기존 파일 정보 제거
            System.out.println("BasicImage: " + BasicImage.BASIC_USER_IMAGE.getPath());
            dto.setProfileImage(BasicImage.BASIC_USER_IMAGE.getPath());
            FileRequestDto profileImage = FileRequestDto.create(TableName.MEMBER_PROFILE_INFO, profile.getId());
            List<FileResponseDto> fileList = fileService.findFilesByTableInfo(profileImage, false);

            if (fileList.size() > 0) {
                FileResponseDto findProfileFile = fileList.get(0);
                fileService.delete(findProfileFile.getFileId());
            }

        }

        // 공통 : MemberProfile 수정 작업
        profile.changProfile(dto);

    }

    /**
     * 회원 탈퇴
     */
    public void updateMemberDelete(Long memberId, HttpServletRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        MemberProfileInfo profile = memberProfileInfoRepository.findByMemberId(memberId, false).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        MemberSecretInfo secret = memberSecretInfoRepository.findByMemberId(memberId, false).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        // 회원 삭제
        member.softDelete();
        profile.softDelete();
        secret.softDelete();

        // Todo: 회원이 등록한 게시글 삭제

        // Todo: 회원이 등록한 강아지 삭제

        try {
            // Refresh 토큰을 Redis에서 제거하는 작업
            redisService.removeRefreshToken("refresh:" + hmacAndBase64.crypt(request.getRemoteAddr(), "HmacSHA512") + "_" + memberId);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ApplicationException(ErrorCode.CRYPT_ERROR);
        }

        // Todo: 회원 프로필, 게시글, 강아지에 대한 파일 삭제
        // 회원 프로필 이미지 삭제
        fileService.deleteFileList(FileRequestDto.create(TableName.MEMBER_PROFILE_INFO, profile.getId()));


    }
}
