package com.doggyWalky.doggyWalky.report.controller;

import com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.response.SimpleReportResponseDto;
import com.doggyWalky.doggyWalky.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/reports")
    public ResponseEntity reports(@RequestBody ReportRequestDto reportDto, Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        SimpleReportResponseDto response = reportService.registerReport(memberId, reportDto);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    // Todo: 검색조건을 사용해서 조회하기
//    @GetMapping("/reports")
//    public ResponseEntity getReportList() {
//        reportService.getReportList();
//    }
}
