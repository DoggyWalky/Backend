package com.doggyWalky.doggyWalky.apply.service;

import com.doggyWalky.doggyWalky.apply.dto.request.NewApplyRequestDto;
import com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.SimpleApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.entity.Apply;
import com.doggyWalky.doggyWalky.apply.repository.ApplyRepository;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ApplyService {

    private final ApplyRepository applyRepository;

    private final JobPostRepository jobPostRepository;

    private final MemberRepository memberRepository;

    /**
     * 산책 신청 등록하기
     */
    public SimpleApplyResponseDto registerApply(NewApplyRequestDto requestDto, Long workerId) {
        Member owner = memberRepository.findByIdNotDeleted(requestDto.getOwnerId()).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        Member worker = memberRepository.findByIdNotDeleted(workerId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(requestDto.getJobPostId()).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));

        // 게시글 작성자와 견주 비교
        if (jobPost.getMember().getId()!=owner.getId()) {
            throw new ApplicationException(ErrorCode.INCORRECT_MATCH_WRITER);
        }

        // 해당 게시글에 대한 신청 여부 확인
        applyRepository.findDuplicateApply(jobPost.getId(), workerId).ifPresent(apply -> {throw new ApplicationException(ErrorCode.ALREADY_REGISTERED_APPLY);});

        Apply apply = Apply.builder().jobPost(jobPost).owner(owner).worker(worker).status(ConstantPool.ApplyStatus.WAIT).build();
        applyRepository.save(apply);
        return new SimpleApplyResponseDto(apply);
    }

    /**
     * 신청 목록 조회
     */
    public List<ApplyResponseDto> getApplyList(Long jobPostId, Long memberId) {
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(jobPostId).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));

        // 게시글 작성자 본인이 아닐경우 예외 발생
        if (jobPost.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_JOBPOST_WRITER);
        }

        return applyRepository.findListAppliedToTheJobPost(jobPost.getId(), jobPost.getMember().getId());

    }
}
