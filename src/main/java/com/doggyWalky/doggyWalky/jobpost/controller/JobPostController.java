package com.doggyWalky.doggyWalky.jobpost.controller;

import com.amazonaws.Response;
import com.doggyWalky.doggyWalky.jobpost.dto.*;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.service.JobPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/job-post")
@Slf4j
@RequiredArgsConstructor
public class JobPostController {
    private final JobPostService jobPostService;
    private final ObjectMapper objectMapper;

    @PostMapping("/register")
    public ResponseEntity<JobPostRegisterResponse> register(
            Principal principal,
            @RequestPart("jobPost") String jobPostStr,
            @RequestPart("images") List<MultipartFile> images) {

        Long memberId = Long.parseLong(principal.getName());

        try {
            JobPostRegisterRequest request = objectMapper.readValue(jobPostStr, JobPostRegisterRequest.class);
            JobPostRegisterResponse response = jobPostService.register(memberId, request, images);

            log.info("성공적으로 등록됨 : {}", memberId);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("게시글 등록 실패: {}, error: {}", memberId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobPost>> searchJobPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startPoint) {
        JobPostSearchCriteria criteria = new JobPostSearchCriteria();
        criteria.setTitle(title);
        criteria.setStatus(status != null ? Status.valueOf(status) : null);
        criteria.setStartPoint(startPoint);

        List<JobPost> jobPosts = jobPostService.searchJobPosts(criteria);
        return ResponseEntity.ok(jobPosts);
    }

    /**
     * 산책 종료하기
     */
    @PostMapping("/{job-post-id}/walk-complete")
    public ResponseEntity<JobPostSimpleResponseDto> walkComplete(@PathVariable("job-post-id") Long jobPostId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        JobPostSimpleResponseDto dto = jobPostService.setWalkingComplete(memberId, jobPostId);
        return new ResponseEntity(dto,HttpStatus.OK);
    }

    /**
     * 실시간 산책 중인 게시글 목록 조회
     */
    @GetMapping("/walking")
    public ResponseEntity<Page<JobPostWalkingResponseDto>> getWalkingList(@PageableDefault Pageable pageable, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        Page<JobPostWalkingResponseDto> jobPostListOnWalking = jobPostService.getJobPostListOnWalking(pageable, memberId);
        return new ResponseEntity<>(jobPostListOnWalking, HttpStatus.OK);
    }



}
