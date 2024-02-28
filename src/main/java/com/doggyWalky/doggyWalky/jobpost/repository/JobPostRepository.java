package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost,Long> {
}
