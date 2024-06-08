package com.doggyWalky.doggyWalky.report.repository;

import com.doggyWalky.doggyWalky.member.entity.QMember;
import com.doggyWalky.doggyWalky.member.entity.QMemberProfileInfo;
import com.doggyWalky.doggyWalky.member.entity.QMemberSecretInfo;
import com.doggyWalky.doggyWalky.report.dto.condition.ReportSearchCondition;
import com.doggyWalky.doggyWalky.report.dto.response.ReportResponseDto;
import com.doggyWalky.doggyWalky.report.entity.QReport;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto.*;
import static com.doggyWalky.doggyWalky.report.entity.QReport.*;
import static org.springframework.util.StringUtils.hasText;

public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QMember reporter = new QMember("reporter");
    private final QMember target = new QMember("target");

    private final QMemberProfileInfo reporterProfile = new QMemberProfileInfo("reporterProfile");

    private final QMemberProfileInfo targetProfile = new QMemberProfileInfo("targetProfile");

    private final QMemberSecretInfo reporterSecret = new QMemberSecretInfo("reporterSecret");

    private final QMemberSecretInfo targetSecret = new QMemberSecretInfo("targetSecret");

    public ReportRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public Page<ReportResponseDto> searchReportList(ReportSearchCondition condition, Pageable pageable) {


        List<ReportResponseDto> content = queryFactory.select(Projections.constructor(ReportResponseDto.class,
                        reporter.id,
                        reporterProfile.nickName,
                        reporterProfile.nickName,
                        reporterProfile.description,
                        reporterProfile.profileImage,
                        reporterSecret.phoneNumber,
                        target.id,
                        targetProfile.nickName,
                        targetProfile.nickName,
                        targetProfile.description,
                        targetProfile.profileImage,
                        targetSecret.phoneNumber,
                        report.jobPost.id,
                        report.type,
                        report.content,
                        report.createdAt
                ))
                .from(report)
                .leftJoin(report.reporter, reporter)
                .leftJoin(report.target, target)
                .leftJoin(reporterProfile).on(reporter.id.eq(reporterProfile.member.id))
                .leftJoin(reporterSecret).on(reporter.id.eq(reporterSecret.member.id))
                .leftJoin(targetProfile).on(target.id.eq(targetProfile.member.id))
                .leftJoin(targetSecret).on(target.id.eq(targetSecret.member.id))
                .where(
                        reporterEq(condition.getReporterId()),
                        targetEq(condition.getTargetId()),
                        reportContentEq(condition.getReportContent()),
                        reportTypeEq(condition.getType()),
                        isWithinStartDateRange(condition.getStartDate()),
                        isWithinEndDateRange(condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int size = queryFactory
                .select(report)
                .from(report)
                .where(
                        reporterEq(condition.getReporterId()),
                        targetEq(condition.getTargetId()),
                        reportContentEq(condition.getReportContent()),
                        reportTypeEq(condition.getType()),
                        isWithinStartDateRange(condition.getStartDate()),
                        isWithinEndDateRange(condition.getEndDate())
                )
                .fetch().size();

        return new PageImpl<>(content, pageable, size);
    }

    private BooleanExpression reporterEq(Long reporterId) {
        return reporterId != null ? reporter.id.eq(reporterId) : null;
    }

    private BooleanExpression targetEq(Long targetId) {
        return targetId != null ? target.id.eq(targetId) : null;
    }

    private BooleanExpression reportContentEq(String reportContent) {
        return hasText(reportContent) ? report.content.like(reportContent) : null;
    }

    private BooleanExpression reportTypeEq(Type reportType) {
        return reportType != null ? report.type.eq(reportType) : null;
    }

    private BooleanExpression isWithinStartDateRange(LocalDateTime startDate) {
        return startDate !=null ? report.createdAt.goe(startDate) : null;
    }

    private BooleanExpression isWithinEndDateRange(LocalDateTime endDate) {
        return endDate !=null ? report.createdAt.loe(endDate) : null;
    }
}
