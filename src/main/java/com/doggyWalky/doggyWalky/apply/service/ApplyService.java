package com.doggyWalky.doggyWalky.apply.service;

import com.doggyWalky.doggyWalky.apply.dto.request.NewApplyRequestDto;
import com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.MyApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.SimpleApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.entity.Apply;
import com.doggyWalky.doggyWalky.apply.repository.ApplyRepository;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

        // 게시글 작성자가 신청한건지 확인
        if (jobPost.getMember().getId()==workerId) {
            throw new ApplicationException(ErrorCode.NOT_APPLY_SELF);
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
    @Transactional(readOnly = true)
    public List<ApplyResponseDto> getApplyList(Long jobPostId, Long memberId) {
        JobPost jobPost = jobPostRepository.findJobPostByIdNotDeleted(jobPostId).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));

        // 게시글 작성자 본인이 아닐경우 예외 발생
        if (jobPost.getMember().getId() != memberId) {
            throw new ApplicationException(ErrorCode.NOT_JOBPOST_WRITER);
        }

        return applyRepository.findListAppliedToTheJobPost(jobPost.getId(), jobPost.getMember().getId());

    }


    /**
     * 신청 거절하기
     */
    public SimpleApplyResponseDto refuseApply(Long applyId, Long memberId) {

        // 해당 신청을 조회하여 견주인지 체크
        Apply findApply = applyRepository.findByOwner(applyId, memberId).orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_APPLY_PERMISSION));

        // 수락상태 여부 수정하기
        findApply.changeApplyStatus(ConstantPool.ApplyStatus.REFUSE);

        // 만약 구인글 구인상태가 예약일 시 대기 상태로 수정하기
        JobPost findJobPost = jobPostRepository.findJobPostByIdNotDeleted(findApply.getJobPost().getId()).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));
        if (findJobPost.getStatus() == Status.RESERVED) {
            findJobPost.setStatus(Status.WAITING);
        }

        return new SimpleApplyResponseDto(findApply);

    }

    /**
     * 신청 수락하기
     */
    public SimpleApplyResponseDto acceptApply(Long applyId, Long memberId) {

        // 해당 신청을 조회하여 견주인지 체크
        Apply findApply = applyRepository.findByOwner(applyId, memberId).orElseThrow(() -> new ApplicationException(ErrorCode.INVALID_APPLY_PERMISSION));

        // 해당 게시글에 수락된 신청이 이미 존재할 경우 예외 발생
        if (applyRepository.findAcceptedApplyByJobPostId(findApply.getJobPost().getId()).isPresent()) {
            throw new ApplicationException(ErrorCode.ACCEPT_APPLY_EXISTS);
        }

        // 해당 신청의 수락 상태를 예약으로 변경
        findApply.changeApplyStatus(ConstantPool.ApplyStatus.ACCEPT);

        // 구인글 구인상태가 예약으로 수정하기
        JobPost findJobPost = jobPostRepository.findJobPostByIdNotDeleted(findApply.getJobPost().getId()).orElseThrow(() -> new ApplicationException(ErrorCode.JOBPOST_NOT_FOUND));
        findJobPost.setStatus(Status.RESERVED);

        return new SimpleApplyResponseDto(findApply);


    }

    public Page<MyApplyResponseDto> getMyApplyList(Long memberId, Pageable pageable) {
        memberRepository.findByIdNotDeleted(memberId).orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_FOUND));

        Page<MyApplyResponseDto> myApplyList = applyRepository.getMyApplyList(memberId, pageable);
        return myApplyList;
    }
}
