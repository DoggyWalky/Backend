package com.doggyWalky.doggyWalky.apply.dto.response;

import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import lombok.Getter;

@Getter
public class MyApplyResponseDto {

    // 게시글 작성자 id
    Long writerId;

    // 게시글 작성자 닉네임
    String nickName;

    // 게시글 작성자 프로필 이미지
    String profileImage;

    // 게시글 id
    Long jobPostId;

    // 게시글 제목
    String title;

    // 게시글 이미지
    String postImage;

    // 게시글 구인 상태
    Status status;

    // 강아지 id
    Long dogId;

    // 강아지 분류
    String kind;

    // 신청 id
    Long applyId;

    // 신청 상태
    ConstantPool.ApplyStatus applyStatus;

    public MyApplyResponseDto(Long writerId, String nickName, String profileImage, Long jobPostId, String title, String postImage, Status status, Long dogId, String kind, Long applyId, ConstantPool.ApplyStatus applyStatus) {
        this.writerId = writerId;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.jobPostId = jobPostId;
        this.title = title;
        this.postImage = postImage;
        this.status = status;
        this.dogId = dogId;
        this.kind = kind;
        this.applyId = applyId;
        this.applyStatus = applyStatus;
    }
}
