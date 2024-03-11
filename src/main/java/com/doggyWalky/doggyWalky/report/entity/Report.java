package com.doggyWalky.doggyWalky.report.entity;

import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto.*;

@Entity
@Table(name="report_list")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="reporter_id")
    private Member reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_id")
    private Member target;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="jobpost_id")
    private JobPost jobPost;

    @Column(name="report_type")
    private Type type;

    @Column(name="report_content")
    private String content;

    @Column(updatable = false, name="created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
    }

    public Report(Member reporter, Member target, JobPost jobPost, Type type, String content) {
        this.reporter = reporter;
        this.target = target;
        this.jobPost = jobPost;
        this.type = type;
        this.content = content;
    }
}
