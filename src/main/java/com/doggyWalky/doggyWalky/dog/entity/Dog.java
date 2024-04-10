package com.doggyWalky.doggyWalky.dog.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dogId")
    private Long dogId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String weight;

    private String description;

    private String name;



}
