package com.doggyWalky.doggyWalky.report.service;

import com.doggyWalky.doggyWalky.chat.dto.response.ChatMessageResponse;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import com.doggyWalky.doggyWalky.report.dto.condition.ReportSearchCondition;
import com.doggyWalky.doggyWalky.report.dto.request.ChatReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.response.ReportResponseDto;
import com.doggyWalky.doggyWalky.report.dto.response.SimpleReportResponseDto;
import com.doggyWalky.doggyWalky.report.entity.Report;
import com.doggyWalky.doggyWalky.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;

    private final MemberRepository memberRepository;

    private final JobPostRepository jobPostRepository;


    /**
     * 신고 등록하기
     */
    public SimpleReportResponseDto registerReport(Long memberId, ReportRequestDto requestDto) {
        Member reportMember = memberRepository.findByIdNotDeleted(memberId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Member targetMember = memberRepository.findByIdNotDeleted(requestDto.getTargetId()).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(requestDto.getJobPostId()).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));

        Report report = new Report(reportMember, targetMember, jobPost, requestDto.getType(), requestDto.getReportContent());

        reportRepository.save(report);

        return new SimpleReportResponseDto(report.getId());
    }

    public Page<ReportResponseDto> getReportList(ReportSearchCondition condition, Pageable pageable) {
        return reportRepository.searchReportList(condition, pageable);
    }

    /**
     * 채팅 신고에서 채팅 목록 들고오는 로직
     */
    public List<ChatMessageResponse> getChatListForReports(ChatReportRequestDto dto, Long reportId) {
        reportRepository.findById(reportId).orElseThrow(() -> new ApplicationException(ErrorCode.REPORT_NOT_FOUND));

        memberRepository.findById(dto.getReporterId()).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        memberRepository.findById(dto.getTargetId()).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        jobPostRepository.findJobPostByIdNotDeleted(dto.getJobPostId()).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));

        return reportRepository.getChatMessagesForReport(dto.getReporterId(), dto.getTargetId(), dto.getJobPostId());


    }
}
