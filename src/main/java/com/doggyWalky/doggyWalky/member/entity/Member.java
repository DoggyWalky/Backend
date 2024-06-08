package com.doggyWalky.doggyWalky.member.entity;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="role_id")
    private Role role;

    private LocalDateTime createdAt;

    private boolean deletedYn;

    public Member(String email,String name, Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.deletedYn = false;
    }

    public void softDelete() {
        this.deletedYn = true;
    }
}
