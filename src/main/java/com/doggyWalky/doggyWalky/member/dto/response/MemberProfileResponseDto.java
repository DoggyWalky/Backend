package com.doggyWalky.doggyWalky.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class MemberProfileResponseDto {

    private Long memberId;

    private String nickName;

    private String description;

    private String createdAt;

    private String updatedAt;

    //Todo: 파일 이미지 추가
//    private String profileImage

    public MemberProfileResponseDto(Long memberId, String nickName, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.description = description;
        this.createdAt = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.updatedAt = updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
