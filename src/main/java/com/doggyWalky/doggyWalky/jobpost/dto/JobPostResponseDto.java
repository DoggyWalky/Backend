package com.doggyWalky.doggyWalky.jobpost.dto;

import com.doggyWalky.doggyWalky.dog.entity.DogSize;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.entity.WalkingProcessStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class JobPostResponseDto {
    private Long id;
    private String title;
    private String content;
    private Status status;
    private WalkingProcessStatus walkingProcessStatus;
    private String startPoint;
    private String bcode;
    private Long dogId;
    private String defaultImage;
    private Boolean deletedYn;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdDate;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime updatedDate;
    private DogSize dogSize;
}
