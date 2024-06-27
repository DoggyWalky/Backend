package com.doggyWalky.doggyWalky.jobpost.dto.response;

import com.doggyWalky.doggyWalky.dog.entity.DogSize;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class JobPostDetailResponseDto {

    private Long memberId;
    private String memberProfileImage;
    private String memberNickname;
    private Long postId;
    private String title;
    private String postImage;
    private String content;
    private Status status;
    private String startPoint;
    private String bcode;
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime createdDate;
    private Long dogId;
    private String dogProfile;
    private String dogName;
    private DogSize dogSize;
}
