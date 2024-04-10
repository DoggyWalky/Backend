package com.doggyWalky.doggyWalky.review.service;


import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import com.doggyWalky.doggyWalky.review.dto.RegisterReviewRequest;
import com.doggyWalky.doggyWalky.review.dto.RegisterReviewResponse;
import com.doggyWalky.doggyWalky.review.entity.Review;
import com.doggyWalky.doggyWalky.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final MemberService memberService;

    public RegisterReviewResponse registerReview(Long memberId, RegisterReviewRequest registerReviewRequest) {

        Member byMemberId = memberService.findByMemberId(memberId);

        Review review = registerReviewRequest.toEntity(byMemberId);

        return new RegisterReviewResponse(reviewRepository.save(review));
    }
}
