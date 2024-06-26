package com.doggyWalky.doggyWalky.jobpost.dto.request;

import com.doggyWalky.doggyWalky.file.entity.File;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.entity.WalkingProcessStatus;
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

    private String startPoint;

    private String bcode;

    private Long dogId;

    public JobPost toEntity(){
        return JobPost
                .builder()
                .title(this.title)
                .content(this.content)
                .status(Status.WAITING)
                .startPoint(this.startPoint)
                .bcode(this.bcode)
                .deletedYn(false)
                .walkingProcessStatus(WalkingProcessStatus.PREWALK)
                .build();
    }

}
