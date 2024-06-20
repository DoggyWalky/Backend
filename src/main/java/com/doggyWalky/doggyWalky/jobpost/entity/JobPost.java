package com.doggyWalky.doggyWalky.jobpost.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.dog.entity.Dog;
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

    @Enumerated(EnumType.STRING)
    @Column(name="job_offer_status")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name="walk_process_status")
    private WalkingProcessStatus walkingProcessStatus;

    @Column(name = "start_point")
    private String startPoint;

    @Column(name = "bcode")
    private String bcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="dog_id")
    private Dog dog;


    private String defaultImage;

    @Column(name="deleted_yn")
    private Boolean deletedYn;

    @Builder
    public JobPost(String title, String content, Status status,WalkingProcessStatus walkingProcessStatus, String startPoint, String bcode, Dog dog, Boolean deletedYn) {
        this.title = title;
        this.content = content;
        this.status = status;
        this.walkingProcessStatus = walkingProcessStatus;
        this.startPoint = startPoint;
        this.bcode = bcode;
        this.dog = dog;
        this.deletedYn = deletedYn;
    }

    public void assignWriter(Member member){
        this.member = member;
    }

    public void setDefaultImage(String path){
        this.defaultImage = path;
    }

    public void setWalkingStatus(WalkingProcessStatus processStatus) {
        this.walkingProcessStatus = processStatus;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setDog(Dog dog) {
        this.dog = dog;}

}
