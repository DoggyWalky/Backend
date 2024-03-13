package com.doggyWalky.doggyWalky.apply.dto.response;

import com.doggyWalky.doggyWalky.constant.ConstantPool;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApplyResponseDto {

    private Long applyId;

    private Long jobPostId;

    private Long ownerId;

    private Long workerId;

    private String workerName;

    private String workerNickName;

    private String workerDescription;

    private String workerProfileImage;

    private ConstantPool.ApplyStatus status;

    private String createdAt;

    private String updatedAt;

    public ApplyResponseDto(Long applyId, Long jobPostId, Long ownerId, Long workerId, String workerName, String workerNickName, String workerDescription, String workerProfileImage, ConstantPool.ApplyStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.applyId = applyId;
        this.jobPostId = jobPostId;
        this.ownerId = ownerId;
        this.workerId = workerId;
        this.workerName = workerName;
        this.workerNickName = workerNickName;
        this.workerDescription = workerDescription;
        this.workerProfileImage = workerProfileImage;
        this.status = status;
        this.createdAt = createdAt.toString().replace("T"," ");
        this.updatedAt = updatedAt.toString().replace("T"," ");
    }
}
