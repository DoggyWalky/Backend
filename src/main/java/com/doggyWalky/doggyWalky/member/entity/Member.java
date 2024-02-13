package com.doggyWalky.doggyWalky.member.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
