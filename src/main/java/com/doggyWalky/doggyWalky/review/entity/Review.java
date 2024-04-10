package com.doggyWalky.doggyWalky.review.entity;


import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private String description;

    private int score;

}
