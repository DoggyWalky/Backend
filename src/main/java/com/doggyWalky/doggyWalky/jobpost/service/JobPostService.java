package com.doggyWalky.doggyWalky.jobpost.service;

import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.file.common.BasicImage;
import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import com.doggyWalky.doggyWalky.file.service.FileService;
import com.doggyWalky.doggyWalky.jobpost.dto.*;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.WalkingProcessStatus;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostSpecifications;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobPostService {

    private final JobPostRepository jobPostRepository;

    private final MemberService memberService;

    private final FileService fileService;

    @Transactional
    public JobPostRegisterResponse register(Long memberId, JobPostRegisterRequest jobPostRegisterRequest, List<MultipartFile> images) {

        Member member = memberService.findByMemberId(memberId);
        JobPost jobPost = jobPostRegisterRequest.toEntity();
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

    public List<JobPost> searchJobPosts(JobPostSearchCriteria criteria) {
        return jobPostRepository.findAll(Specification.where(
                JobPostSpecifications.withDynamicQuery(
                        criteria.getTitle(),
                        criteria.getStatus(),
                        criteria.getStartPoint()
                )
        ));
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
