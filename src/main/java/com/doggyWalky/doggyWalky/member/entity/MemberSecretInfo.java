package com.doggyWalky.doggyWalky.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSecretInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_secret_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private String address;

    private String phoneNumber;

    public MemberSecretInfo(Member member, String address, String phoneNumber) {
        this.member = member;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    private void changeSecretInfo(String address, String phoneNumber) {
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
