package com.doggyWalky.doggyWalky.jobpost.controller;

import com.doggyWalky.doggyWalky.jobpost.dto.JobPostRegisterRequest;
import com.doggyWalky.doggyWalky.jobpost.dto.JobPostRegisterResponse;
import com.doggyWalky.doggyWalky.jobpost.service.JobPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
