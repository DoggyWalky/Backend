package com.doggyWalky.doggyWalky.member.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.file.common.BasicImage;
import com.doggyWalky.doggyWalky.member.dto.request.MemberPatchProfileDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberProfileInfo extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_profile_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private String nickName;

    private String description;


    private boolean deletedYn;

    private String profileImage;

    public MemberProfileInfo(Member member) {
        this.member = member;
        this.nickName = member.getName();
        this.description = "기본 자기 소개글을 작성해주시기 바랍니다.";
        this.deletedYn = false;
        this.profileImage = BasicImage.BASIC_USER_IMAGE.getPath();
    }


    public MemberProfileInfo(Member member,String nickName, String description) {
        this.member = member;
        this.nickName = nickName;
        this.description = description;
    }

    public void changeProfile(String nickName, String description) {
        this.nickName = nickName;
        this.description = description;
    }

    public void changeProfile(MemberPatchProfileDto dto) {
        this.profileImage = dto.getProfileImage();
        this.description = dto.getDescription();
        this.nickName = dto.getNickName();
    }

    public void softDelete() {
        this.deletedYn = true;
    }
}
