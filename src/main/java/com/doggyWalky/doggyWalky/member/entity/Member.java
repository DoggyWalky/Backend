package com.doggyWalky.doggyWalky.member.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String email;
    private String name;

    private LocalDateTime createdAt;

    public Member(String email,String name) {
        this.email = email;
        this.name = name;
        createdAt = LocalDateTime.now();
    }
}
