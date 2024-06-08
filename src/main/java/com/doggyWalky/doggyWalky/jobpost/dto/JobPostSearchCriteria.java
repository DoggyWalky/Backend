package com.doggyWalky.doggyWalky.jobpost.dto;

import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class JobPostSearchCriteria {
    private String title;
    private Status status;
    private String region;
    private String startPoint;
}