package com.doggyWalky.doggyWalky.jobpost.service;

import com.doggyWalky.doggyWalky.apply.repository.ApplyRepository;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.dog.entity.Dog;
import com.doggyWalky.doggyWalky.dog.entity.DogSize;
import com.doggyWalky.doggyWalky.dog.repository.DogRepository;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.file.common.BasicImage;
import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.request.FileRequestDto;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import com.doggyWalky.doggyWalky.file.service.FileService;
import com.doggyWalky.doggyWalky.gps.repository.GpsRepository;
import com.doggyWalky.doggyWalky.jobpost.dto.request.JobPostPatchDto;
import com.doggyWalky.doggyWalky.jobpost.dto.request.JobPostRegisterRequest;
import com.doggyWalky.doggyWalky.jobpost.dto.request.JobPostSearchCriteria;
import com.doggyWalky.doggyWalky.jobpost.dto.response.*;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.WalkingProcessStatus;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostSpecifications;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.entity.MemberProfileInfo;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import com.doggyWalky.doggyWalky.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;

    private final DogRepository dogRepository;

    private final ApplyRepository applyRepository;

    private final ReportRepository reportRepository;

    private final GpsRepository gpsRepository;


    private final MemberService memberService;

    private final FileService fileService;

    @Transactional
    public JobPostRegisterResponse register(Long memberId, JobPostRegisterRequest jobPostRegisterRequest, List<MultipartFile> images) {

        Member member = memberService.findByMemberId(memberId);
        JobPost jobPost = jobPostRegisterRequest.toEntity();
        Dog dog = dogRepository.findById(jobPostRegisterRequest.getDogId()).get();
        jobPost.setDog(dog);
        jobPost.assignWriter(member);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        if (images != null && !images.isEmpty() && images.get(0).getOriginalFilename().length()!=0) {

            saveImages(images, savedJobPost);
        } else {

            savedJobPost.setDefaultImage(BasicImage.BASIC_JOB_POST_IMAGE.getPath());

            jobPostRepository.save(savedJobPost);

        }
        return new JobPostRegisterResponse(savedJobPost);
    }

    /**
     * 게시글 수정하기
     */
    @Transactional
    public void updateJobPost(Long memberId, Long jobPostId, JobPostPatchDto dto) {
        // 수정하고 싶은 게시글 엔티티 찾아오기
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(jobPostId).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));
        // 게시글 작성자가 맞는지 확인
        if (jobPost.getMember().getId()!=memberId) {
            throw new ApplicationException(ErrorCode.NOT_JOBPOST_WRITER);
        }

        // 프로필 이미지 - fileId 값이 있는 경우
        if (dto.getFileId() != null) {

            // 게시글 이미지 세팅
            File file = fileService.findFile(dto.getFileId());
            fileService.checkFileExtForProfile(file.getFileName());

            // 기본이미지 아닐 경우 기존 파일 정보 삭제
            if (!jobPost.getDefaultImage().equals(BasicImage.BASIC_JOB_POST_IMAGE.getPath())) {
                FileRequestDto profileImage = FileRequestDto.create(TableName.JOB_POST, jobPost.getId());
                FileResponseDto findProfileFile = fileService.findFilesByTableInfo(profileImage, false).get(0);
                fileService.delete(findProfileFile.getFileId());
            }

            // 공통 : 파일 정보 저장
            fileService.saveFileInfo(new FileInfo(TableName.JOB_POST, jobPost.getId(), file));
            dto.setProfileImage(file.getPath());

        } else if (!StringUtils.hasText(dto.getProfileImage())) {
            // 사진을 제거한 상태이기 때문에 기본 이미지로 세팅해주는 작업
            // 기존 파일 정보 제거
            dto.setProfileImage(BasicImage.BASIC_JOB_POST_IMAGE.getPath());
            FileRequestDto profileImage = FileRequestDto.create(TableName.JOB_POST, jobPost.getId());
            List<FileResponseDto> fileList = fileService.findFilesByTableInfo(profileImage, false);

            if (fileList.size() > 0) {
                FileResponseDto findProfileFile = fileList.get(0);
                fileService.delete(findProfileFile.getFileId());
            }

        }

        // 강아지 엔티티 찾기(산책시킬 강아지를 바꿨을 수 있으니)
        Dog dog = dogRepository.findById(dto.getDogId()).orElseThrow(() -> new ApplicationException(ErrorCode.DOG_NOT_FOUND));

        // 공통 : JobPost 수정 작업
        jobPost.changeJobPost(dto, dog);

    }

    /**
     * 게시글 삭제하기
     */
    @Transactional
    public void deleteJobPost(Long memberId, Long jobPostId) {

        // 게시글 작성자 본인이 맞는지
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(jobPostId).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));
        if(jobPost.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_JOBPOST_WRITER);
        }

        // 게시글 삭제
        jobPost.delete();

        // 게시글 관련 파일, 신청, 신고, GPS, 채팅, 좋아요 삭제
        // 파일 삭제
        fileService.deleteFileList(FileRequestDto.create(TableName.JOB_POST, jobPost.getId()));
        // 신청 삭제
        applyRepository.deleteByJobPostId(jobPostId);
        // 신고 삭제
        reportRepository.deleteByJobPostId(jobPostId);
        // GPS 삭제
        gpsRepository.deleteByJobPostId(jobPostId);

        // Todo:채팅 삭제

        // Todo: 좋아요 삭제

    }

    public List<JobPostResponseDto> searchJobPosts(JobPostSearchCriteria criteria) {
        Specification<JobPost> specification = JobPostSpecifications.withDynamicQuery(
                criteria.getTitle(),
                criteria.getStatuses(),
                criteria.getDogSizes(),
                criteria.getBcode(),
                criteria.getSortOption()
        );

        List<JobPost> jobPosts = jobPostRepository.findAll(specification);

        return jobPosts.stream()
                .map(jobPost -> {
                    DogSize dogSize = null;
                    Dog dog = jobPost.getDog();
                    if (dog != null) {
                        dogSize = dog.getDogSize();
                    }
                    return new JobPostResponseDto(
                            jobPost.getId(),
                            jobPost.getTitle(),
                            jobPost.getContent(),
                            jobPost.getStatus(),
                            jobPost.getWalkingProcessStatus(),
                            jobPost.getStartPoint(),
                            jobPost.getBcode(),
                            jobPost.getDog().getDogId(),
                            jobPost.getDefaultImage(),
                            jobPost.getDeletedYn(),
                            jobPost.getCreatedDate(),
                            jobPost.getUpdatedDate(),
                            dogSize
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 게시글 상세 조회
     */
    public JobPostDetailResponseDto getJobPostDetail(Long jobPostId) {
        JobPostDetailResponseDto jobPostDetailResponseDto = jobPostRepository.findJobPostDetailByPostId(jobPostId).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));
        return jobPostDetailResponseDto;
    }

    /**
     * 내가 작성한 게시글 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<MyJobPostResponseDto>  getMyPostList(Long memberId, Pageable pageable) {
        return jobPostRepository.findListAppliedToTheJobPost(memberId, pageable);
    }


    /**
     * 게시글 산책 진행 상태 완료 변경 로직
     */
    @Transactional
    public JobPostSimpleResponseDto setWalkingComplete(Long memberId, Long jobPostId) {
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(jobPostId).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));

        // 해당 게시글 작성자가 아닐 경우 에러 반환
        if (jobPost.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_JOBPOST_WRITER);
        }

        jobPost.setWalkingStatus(WalkingProcessStatus.POSTWALK);
        return new JobPostSimpleResponseDto(jobPost.getId());
    }

    /**
     * 현재 산책 중인 게시글 목록 조회하기
     */
    @Transactional(readOnly = true)
    public Page<JobPostWalkingResponseDto> getJobPostListOnWalking(Pageable pageable, Long memberId) {
        return jobPostRepository.findJobPostByWalkProcessStatus(memberId, WalkingProcessStatus.WALKING, ConstantPool.ApplyStatus.ACCEPT, pageable);
    }


    private void saveImages(List<MultipartFile> images, JobPost savedJobPost) {

        List<FileResponseDto> uploadedFiles = fileService.uploadFile(images);

        List<FileInfo> fileInfos = uploadedFiles.stream().map(fileResponseDto -> {
            File file = fileService.findFile(fileResponseDto.getFileId());

            return new FileInfo(TableName.JOB_POST, savedJobPost.getId(), file);

        }).collect(Collectors.toList());

        fileService.saveFileInfoList(fileInfos);

    }






}
