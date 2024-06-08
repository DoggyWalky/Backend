package com.doggyWalky.doggyWalky.report.controller;

import com.doggyWalky.doggyWalky.chat.dto.response.ChatMessageResponse;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.report.dto.condition.ReportSearchCondition;
import com.doggyWalky.doggyWalky.report.dto.request.ChatReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.response.ReportResponseDto;
import com.doggyWalky.doggyWalky.report.dto.response.SimpleReportResponseDto;
import com.doggyWalky.doggyWalky.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    // Todo: 테스트 필요
    @PostMapping("/reports")
    public ResponseEntity reports(@RequestBody @Valid ReportRequestDto reportDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasFieldErrors("reportContent")) {
            throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_REPORTCONTENT);
        }
        Long memberId = Long.parseLong(principal.getName());
        SimpleReportResponseDto response = reportService.registerReport(memberId, reportDto);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    // Todo: 검색조건을 사용해서 조회하기
    @GetMapping("/reports")
    public Page<ReportResponseDto> getReportList(ReportSearchCondition condition, @PageableDefault Pageable pageable) {
        return reportService.getReportList(condition, pageable);
    }

    @GetMapping("/reports/{report-id}/chat-list")
    public ResponseEntity getChatListForReports(@PathVariable("report-id") Long reportId,
                                                @RequestBody ChatReportRequestDto dto) {
        List<ChatMessageResponse> chatLists = reportService.getChatListForReports(dto, reportId);
        return new ResponseEntity(chatLists, HttpStatus.OK);

    }
}
