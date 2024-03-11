package com.doggyWalky.doggyWalky.report.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRequestDto {

    public enum Type {
        ABUSE, NONFULFILMENT, PUPPYLOST
    }

    private Long targetId;

    private Long jobPostId;

    private String reportContent;

    private  Type type;



}
