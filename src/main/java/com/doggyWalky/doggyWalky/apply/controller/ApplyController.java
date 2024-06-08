package com.doggyWalky.doggyWalky.apply.controller;

import com.doggyWalky.doggyWalky.apply.dto.request.NewApplyRequestDto;
import com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.MyApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.SimpleApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/apply")
public class ApplyController {

    private final ApplyService applyService;

    /**
     * 신청 등록하기
     */
    // Todo: 테스트 미완료
    @PostMapping()
    public ResponseEntity<SimpleApplyResponseDto> apply(@RequestBody NewApplyRequestDto requestDto, Principal principal) {
        Long workerId = Long.parseLong(principal.getName());
        SimpleApplyResponseDto applyResponseDto = applyService.registerApply(requestDto, workerId);
        return new ResponseEntity(applyResponseDto, HttpStatus.CREATED);
    }

    /**
     * 신청 목록 조회하기
     */
    // Todo: 페이징 처리하기, 테스트 미완료
    @GetMapping("/job-post/{job-post-id}")
    public ResponseEntity<List<ApplyResponseDto>> getApplyList(@PathVariable("job-post-id") Long jobPostId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        List<ApplyResponseDto> applyList = applyService.getApplyList(jobPostId, memberId);
        return new ResponseEntity<>(applyList, HttpStatus.OK);
    }

    /**
     * 신청 수락하기
     */
    @PostMapping("/{apply-id}/accept")
    public ResponseEntity<SimpleApplyResponseDto> acceptApply(@PathVariable("apply-id") Long applyId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        SimpleApplyResponseDto response = applyService.acceptApply(applyId, memberId);
        return ResponseEntity.ok(response);
    }


    /**
     * 신청 거절하기
     */
    @PostMapping("/{apply-id}/refuse")
    public ResponseEntity<SimpleApplyResponseDto> refuseApply(@PathVariable("apply-id") Long applyId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        SimpleApplyResponseDto response = applyService.refuseApply(applyId, memberId);
        return ResponseEntity.ok(response);
    }


    // Todo: 내가 신청한 목록 조회 API 작성
    @GetMapping("/my-apply")
    public ResponseEntity getMyApplyList(Principal principal, @PageableDefault(size = 10,sort="createdDate", direction = org.springframework.data.domain.Sort.Direction.DESC) Pageable pageable) {
        Long memberId = Long.parseLong(principal.getName());

        // 기본 Sort 설정
        Sort sort = pageable.getSort();

        // Pageable 객체 생성
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<MyApplyResponseDto> myApplyList = applyService.getMyApplyList(memberId, sortedPageable);
        return new ResponseEntity(myApplyList, HttpStatus.OK);
    }

}
