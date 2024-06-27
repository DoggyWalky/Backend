package com.doggyWalky.doggyWalky.jobpost.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostPatchDto {

    @Size(min = 20, max = 300)
    private String title;

    @Size(min = 20)
    private String content;

    private String startPoint;

    private String bcode;

    private Long dogId;

    private Long fileId;

    @Setter
    private String profileImage;
}
