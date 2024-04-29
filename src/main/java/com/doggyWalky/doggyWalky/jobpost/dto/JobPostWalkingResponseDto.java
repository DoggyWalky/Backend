package com.doggyWalky.doggyWalky.jobpost.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostWalkingResponseDto {

    private Long partMemberId;

    private String partMemberProfileImage;

    private String partMemberNickname;

    private Long dogId;

    private String dogProfileImage;

    private String dogName;

    private Long jobPostId;
}
