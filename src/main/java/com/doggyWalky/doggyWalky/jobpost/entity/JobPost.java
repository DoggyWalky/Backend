package com.doggyWalky.doggyWalky.jobpost.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "jobpost_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    private String title;

    private String content;

    private Status status;

    @Column(name = "start_point")
    private String startPoint;

    @Column(name = "end_point")
    private String endPoint;

    @Column(name = "dog_id" )
    private Long dogId;


    private String defaultImage;

    @Column(name="deleted_yn")
    private Boolean deletedYn;

    @Builder
    public JobPost(String title, String content, Status status, String startPoint, String endPoint, Long dogId, Boolean deletedYn) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.dogId = dogId;
        this.deletedYn = deletedYn;
    }

    public void assignWriter(Member member){
        this.member = member;
    }

    public void setDefaultImage(String path){
        this.defaultImage = path;
    }

}
