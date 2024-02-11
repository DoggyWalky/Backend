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

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private String nickName;

    private String description;

    public MemberProfileInfo(Member member,String nickName, String description) {
        this.member = member;
        this.nickName = nickName;
        this.description = description;
    }

    private void changProfile(String nickName, String description) {
        this.nickName = nickName;
        this.description = description;
    }
}
