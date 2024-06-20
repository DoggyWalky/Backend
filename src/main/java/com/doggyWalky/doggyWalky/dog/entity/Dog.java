package com.doggyWalky.doggyWalky.dog.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Builder
@Getter
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dog_id")
    private Long dogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "dog_size")
    private DogSize dogSize;

    private String weight;

    private String description;

    private String name;

    private String kind;

    private String profileImage;

    // Todo: 강아지 분류 수정
    public void update(String name, String kind, String weight, String description) {
        if (name != null) this.name = name;
        if (kind != null) this.kind = kind;
        if (weight != null) this.weight = weight;
        if (description != null) this.description = description;
    }

}
