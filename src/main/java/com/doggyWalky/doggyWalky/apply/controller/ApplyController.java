package com.doggyWalky.doggyWalky.apply.controller;

import com.doggyWalky.doggyWalky.apply.dto.request.NewApplyRequestDto;
import com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.SimpleApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.service.ApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ApplyController {

    private final ApplyService applyService;

    /**
     * 신청 등록하기
     */
    // Todo: 테스트 미완료
    @PostMapping("/apply")
    public ResponseEntity<SimpleApplyResponseDto> apply(@RequestBody NewApplyRequestDto requestDto, Principal principal) {
        Long workerId = Long.parseLong(principal.getName());
        SimpleApplyResponseDto applyResponseDto = applyService.registerApply(requestDto, workerId);
        return new ResponseEntity(applyResponseDto, HttpStatus.CREATED);
    }

    /**
     * 신청 목록 조회하기
     */
    // Todo: 페이징 처리하기, 테스트 미완료
    @GetMapping("/apply/jobPost/{jobPostId}")
    public ResponseEntity<List<ApplyResponseDto>> getApplyList(@PathVariable Long jobPostId, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        List<ApplyResponseDto> applyList = applyService.getApplyList(jobPostId, memberId);
        return new ResponseEntity<>(applyList, HttpStatus.OK);
    }


}
