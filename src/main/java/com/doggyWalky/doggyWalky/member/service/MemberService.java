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
import com.doggyWalky.doggyWalky.member.entity.MemberProfileInfo;
import com.doggyWalky.doggyWalky.member.repository.MemberProfileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberProfileInfoRepository memberProfileInfoRepository;

    private final FileService fileService;

    /**
     * Member pk로 회원 프로필 조회하기
     */
    public List<MemberProfileResponseDto> getMemberProfiles(Long memberId) {
        List<MemberProfileResponseDto> memberProfiles = memberProfileInfoRepository.findMemberProfiles(memberId, false);
        return memberProfiles;
    }

    // Todo: 테스트 필요
    public void updateMemberProfile(Long memberId, MemberPatchProfileDto dto) {
        MemberProfileInfo profile = memberProfileInfoRepository.findByMemberId(memberId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        // 프로필 이미지 - fileId 값이 있는 경우
        if (dto.getFileId() != null) {

            // 회원 이미지 세팅
            File file = fileService.findFile(dto.getFileId());
            fileService.checkFileExtForProfile(file.getFileName());

            // 기본이미지 아닐 경우 기존 file 삭제
            if (!profile.getProfileImage().equals(BasicImage.BASIC_USER_IMAGE.getPath())) {
                FileRequestDto profileImage = FileRequestDto.create(TableName.MEMBER, memberId);
                FileResponseDto findProfileFile = fileService.findFilesByTableInfo(profileImage, false).get(0);
                fileService.delete(findProfileFile.getFileId());
            }

            fileService.saveFileInfo(new FileInfo(TableName.MEMBER, memberId, file));
            dto.setProfileImage(file.getPath());

        } else if (dto.getProfileImage() == null) {
            dto.setProfileImage(BasicImage.BASIC_USER_IMAGE.getPath());
            FileRequestDto profileImage = FileRequestDto.create(TableName.MEMBER, memberId);
            List<FileResponseDto> fileList = fileService.findFilesByTableInfo(profileImage, false);

            if (fileList.size() > 0) {
                FileResponseDto findProfileFile = fileList.get(0);
                fileService.delete(findProfileFile.getFileId());
            }

        }

        profile.changProfile(dto);

    }
}
