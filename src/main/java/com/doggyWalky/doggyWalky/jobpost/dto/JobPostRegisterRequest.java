package com.doggyWalky.doggyWalky.jobpost.dto;

import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobPostRegisterRequest {

    private String title;

    private String content;

    private Status status;

    private String startPoint;

    private String endPoint;

    private Long dogId;

    public JobPost toEntity(){
        return JobPost
                .builder()
                .title(this.title)
                .content(this.content)
                .status(this.status)
                .startPoint(this.startPoint)
                .endPoint(this.endPoint)
                .dogId(this.dogId)
                .deletedYn(false)
                .build();
    }

}
