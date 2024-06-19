package com.doggyWalky.doggyWalky.jobpost.controller;

import com.doggyWalky.doggyWalky.dog.entity.DogSize;
import com.doggyWalky.doggyWalky.jobpost.dto.*;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.service.JobPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
    public ResponseEntity<List<JobPostResponseDto>> searchJobPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) String bcode,
            @RequestParam(required = false) List<String> dogSize,
            @RequestParam(required = false) String sortOption) {
        log.info("keyword :" + keyword);
        JobPostSearchCriteria criteria = new JobPostSearchCriteria();
        criteria.setTitle(keyword);
        criteria.setStatuses(status != null ? status.stream().map(Status::valueOf).collect(Collectors.toList()) : null);
        criteria.setDogSizes(dogSize != null ? dogSize.stream().map(DogSize::valueOf).collect(Collectors.toList()) : null);
        criteria.setBcode(bcode);
        criteria.setSortOption(sortOption);

        List<JobPostResponseDto> jobPosts = jobPostService.searchJobPosts(criteria);
        return ResponseEntity.ok(jobPosts);
    }

    /**
     * 게시글 상세 조회하기
     */
    @GetMapping("/{job-post-Id}")
    public ResponseEntity<JobPostDetailResponseDto> getPostDetail(@PathVariable("job-post-id") Long jobPostId) {
        JobPostDetailResponseDto dto = jobPostService.getJobPostDetail(jobPostId);
        return new ResponseEntity(dto, HttpStatus.OK);
    }

    /**
     * 내가 작성한 게시글 목록 조회하기
     */
    @GetMapping("/my-post")
    public ResponseEntity<Page<MyJobPostResponseDto>> getMyPostList(Principal principal,@PageableDefault(size = 10,sort="createdDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Long memberId = Long.parseLong(principal.getName());

        // 기본 Sort 설정
        Sort sort = pageable.getSort();

        // Pageable 객체 생성
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<MyJobPostResponseDto> myPostList = jobPostService.getMyPostList(memberId, sortedPageable);
        return new ResponseEntity<>(myPostList, HttpStatus.OK);

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
