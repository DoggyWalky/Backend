package com.doggyWalky.doggyWalky.review.repository;

import com.doggyWalky.doggyWalky.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
}
