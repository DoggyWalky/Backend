package com.doggyWalky.doggyWalky.jobpost.dto.request;

import com.doggyWalky.doggyWalky.dog.entity.DogSize;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JobPostSearchCriteria {
    private String title;
    private String bcode;
    private List<DogSize> dogSizes;
    private List<Status> statuses;
    private String sortOption;
}