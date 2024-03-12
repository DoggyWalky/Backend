package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost,Long> {


    @Query("select jp from JobPost jp where jp.id = :jobPostId and jp.deletedYn=false")
    Optional<JobPost> findJobPostByIdNotDeleted(@Param("jobPostId") Long jobPostId);
}
