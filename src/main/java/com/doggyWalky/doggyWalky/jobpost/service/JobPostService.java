package com.doggyWalky.doggyWalky.jobpost.service;

import com.doggyWalky.doggyWalky.file.common.BasicImage;
import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.response.FileResponseDto;
import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.file.entity.FileInfo;
import com.doggyWalky.doggyWalky.file.service.FileService;
import com.doggyWalky.doggyWalky.jobpost.dto.JobPostRegisterRequest;
import com.doggyWalky.doggyWalky.jobpost.dto.JobPostRegisterResponse;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    private void saveImages(List<MultipartFile> images, JobPost savedJobPost) {

        List<FileResponseDto> uploadedFiles = fileService.uploadFile(images);

        List<FileInfo> fileInfos = uploadedFiles.stream().map(fileResponseDto -> {
            File file = fileService.findFile(fileResponseDto.getFileId());

            return new FileInfo(TableName.JOB_POST, savedJobPost.getId(), file);

        }).collect(Collectors.toList());

        fileService.saveFileInfoList(fileInfos);

    }


}
