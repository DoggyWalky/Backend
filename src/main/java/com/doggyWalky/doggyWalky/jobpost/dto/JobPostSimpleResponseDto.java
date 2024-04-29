package com.doggyWalky.doggyWalky.jobpost.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JobPostSimpleResponseDto {

    private Long jobPostId;

    public JobPostSimpleResponseDto(Long jobPostId) {
        this.jobPostId = jobPostId;
    }
}
