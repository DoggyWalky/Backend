package com.doggyWalky.doggyWalky.jobpost.dto;

import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobPostRegisterResponse {

    private Long jobPostId;

    private Long memberId;

    private String title;

    private String content;

    private String status;

    private String startPoint;

    private String bcode;

    private Long dogId;

    private List<String> images = new ArrayList<>();

    public JobPostRegisterResponse (JobPost jobPost){
        this.jobPostId = jobPost.getId();
        this.memberId = jobPost.getMember().getId();
        this.title = jobPost.getTitle();
        this.content = jobPost.getContent();
        this.status = jobPost.getStatus().getEnStatus();
        this.startPoint = jobPost.getStartPoint();
        this.bcode= jobPost.getBcode();
        this.dogId = jobPost.getDog().getDogId();
    }


}
