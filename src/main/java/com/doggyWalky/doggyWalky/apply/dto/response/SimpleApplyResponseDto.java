package com.doggyWalky.doggyWalky.apply.dto.response;

import com.doggyWalky.doggyWalky.apply.entity.Apply;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import lombok.Getter;

@Getter
public class SimpleApplyResponseDto {

    private Long applyId;

    private Long jobPostId;

    private Long ownerId;

    private Long workerId;

    private ConstantPool.ApplyStatus status;

    private String createdAt;

    private String updatedAt;

    public SimpleApplyResponseDto(Apply apply) {
        this.applyId = apply.getId();
        this.jobPostId = apply.getJobPost().getId();
        this.ownerId = apply.getOwner().getId();
        this.workerId = apply.getWorker().getId();
        this.status = apply.getStatus();
        this.createdAt = apply.getCreatedDate().toString().replace("T", " ");
        this.updatedAt = apply.getUpdatedDate().toString().replace("T", " ");
    }
}
