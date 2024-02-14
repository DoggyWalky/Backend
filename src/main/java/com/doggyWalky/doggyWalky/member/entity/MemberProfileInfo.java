package com.doggyWalky.doggyWalky.member.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public MemberProfileInfo(Member member) {
        this.member = member;
        this.nickName = member.getName();
        this.description = "기본 자기 소개글을 작성해주시기 바랍니다.";
        this.deletedYn = false;
    }


    public MemberProfileInfo(Member member,String nickName, String description) {
        this.member = member;
        this.nickName = nickName;
        this.description = description;
    }

    private void changProfile(String nickName, String description) {
        this.nickName = nickName;
        this.description = description;
    }

    public void softDelete() {
        this.deletedYn = true;
    }
}
