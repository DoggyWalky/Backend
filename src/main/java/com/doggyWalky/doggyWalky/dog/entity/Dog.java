package com.doggyWalky.doggyWalky.dog.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

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
    private Member member;

    private String weight;

    private String description;

    private String name;

    private String kind;

    private String profileImage;

    public void update(String name, String kind, String weight, String description) {
        if (name != null) this.name = name;
        if (kind != null) this.kind = kind;
        if (weight != null) this.weight = weight;
        if (description != null) this.description = description;
    }

}
