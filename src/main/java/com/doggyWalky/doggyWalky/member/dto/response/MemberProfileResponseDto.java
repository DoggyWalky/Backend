package com.doggyWalky.doggyWalky.member.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberProfileResponseDto {

    private Long memberId;

    private String nickName;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    //Todo: 파일 이미지 추가
//    private String profileImage

    public MemberProfileResponseDto(Long memberId, String nickName, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.memberId = memberId;
        this.nickName = nickName;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
