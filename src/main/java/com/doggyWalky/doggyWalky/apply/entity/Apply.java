package com.doggyWalky.doggyWalky.apply.entity;

import com.doggyWalky.doggyWalky.common.entity.BaseEntity;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.constant.ConstantPool.ApplyStatus;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name="apply_info")
@Builder
@AllArgsConstructor
public class Apply extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="apply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="jobpost_id")
    private JobPost jobPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="worker_id")
    private Member worker;

    @Column(name="apply_status")
    @Enumerated(EnumType.STRING)
    private ApplyStatus status;

}
