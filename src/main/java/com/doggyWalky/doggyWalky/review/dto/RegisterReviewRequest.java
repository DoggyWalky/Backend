package com.doggyWalky.doggyWalky.review.dto;


import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterReviewRequest {

    private String description;

    private int score;

    public Review toEntity(Member member) {
        return Review.builder().member(member).description(description).score(validateScore(score)).build();
    }

    public int validateScore(int score) {
        if (score < 1 || score > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "score should over 0 and under 6 ");
        }
        return score;


    }
}
