package com.doggyWalky.doggyWalky.report.dto.condition;

import com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportSearchCondition {

    private Long reporterId;
    private Long targetId;
    private ReportRequestDto.Type type;
    private String reportContent;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
