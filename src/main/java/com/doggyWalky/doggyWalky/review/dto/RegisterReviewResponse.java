package com.doggyWalky.doggyWalky.review.dto;

import com.doggyWalky.doggyWalky.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterReviewResponse {

    private Long reviewId;

    private String description;

    private int score;

    public RegisterReviewResponse(Review review){
        this.reviewId = review.getReviewId();
        this.description = review.getDescription();
        this.score = review.getScore();
    }
}
