package com.doggyWalky.doggyWalky.report.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

import static com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto.*;

@Getter
public class ReportResponseDto {

    private Long reporterId;

    private String reporterName;

    private String reporterNickName;

    private String reporterDescription;

    private String reporterProfileImage;

    private String reporterPhoneNumber;

    private Long targetId;

    private String targetName;

    private String targetNickName;

    private String targetDescription;

    private String targetProfileImage;

    private String targetPhoneNumber;

    private Long jobPostId;

    private Type type;

    private String reportContent;

    private String createdAt;

    public ReportResponseDto(Long reporterId, String reporterName, String reporterNickName, String reporterDescription, String reporterProfileImage, String reporterPhoneNumber, Long targetId, String targetName, String targetNickName, String targetDescription, String targetProfileImage, String targetPhoneNumber, Long jobPostId, Type type, String reportContent, LocalDateTime createdAt) {
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.reporterNickName = reporterNickName;
        this.reporterDescription = reporterDescription;
        this.reporterProfileImage = reporterProfileImage;
        this.reporterPhoneNumber = reporterPhoneNumber;
        this.targetId = targetId;
        this.targetName = targetName;
        this.targetNickName = targetNickName;
        this.targetDescription = targetDescription;
        this.targetProfileImage = targetProfileImage;
        this.targetPhoneNumber = targetPhoneNumber;
        this.jobPostId = jobPostId;
        this.type = type;
        this.reportContent = reportContent;
        this.createdAt = createdAt.toString().replace("T", " ");
    }
}
