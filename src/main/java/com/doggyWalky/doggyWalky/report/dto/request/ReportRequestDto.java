package com.doggyWalky.doggyWalky.report.dto.request;

import jakarta.validation.constraints.Size;
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

    @Size(min = 20, max = 500)
    private String reportContent;

    private  Type type;



}
