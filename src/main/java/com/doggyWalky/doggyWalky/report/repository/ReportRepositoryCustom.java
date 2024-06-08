package com.doggyWalky.doggyWalky.report.repository;

import com.doggyWalky.doggyWalky.report.dto.condition.ReportSearchCondition;
import com.doggyWalky.doggyWalky.report.dto.response.ReportResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReportRepositoryCustom {

    public Page<ReportResponseDto> searchReportList(ReportSearchCondition condition, Pageable pageable);
}
