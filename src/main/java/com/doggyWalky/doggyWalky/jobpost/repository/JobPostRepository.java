package com.doggyWalky.doggyWalky.jobpost.repository;

import com.doggyWalky.doggyWalky.constant.ConstantPool.ApplyStatus;
import com.doggyWalky.doggyWalky.jobpost.dto.response.JobPostDetailResponseDto;
import com.doggyWalky.doggyWalky.jobpost.dto.response.JobPostWalkingResponseDto;
import com.doggyWalky.doggyWalky.jobpost.dto.response.MyJobPostResponseDto;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.WalkingProcessStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JpaSpecificationExecutor<JobPost>, JobPostRepositoryCustom {


    @Query("select jp from JobPost jp where jp.id = :jobPostId and jp.deletedYn=false")
    Optional<JobPost> findJobPostByIdNotDeleted(@Param("jobPostId") Long jobPostId);

    @Query("select new com.doggyWalky.doggyWalky.jobpost.dto.response.JobPostWalkingResponseDto(mpi.member.id, mpi.profileImage, mpi.nickName, d.dogId, d.profileImage, d.name, jp.id) from JobPost jp " +
            "join Apply a on a.jobPost.id = jp.id and a.status = :applyStatus " +
            "join MemberProfileInfo mpi on mpi.member.id = a.worker.id and mpi.deletedYn = false " +
            "join Dog d on d.dogId = jp.dog.dogId " +
            "where jp.deletedYn = false and jp.walkingProcessStatus = :walkStatus and jp.member.id = :memberId")
    Page<JobPostWalkingResponseDto> findJobPostByWalkProcessStatus(@Param("memberId") Long memberId,
                                                                   @Param("walkStatus") WalkingProcessStatus walkStatus,
                                                                   @Param("applyStatus") ApplyStatus applyStatus,
                                                                   Pageable pageable);

    @Query("select new com.doggyWalky.doggyWalky.jobpost.dto.response.MyJobPostResponseDto(jp.id, jp.title,jp.defaultImage,jp.status,d.dogSize,jp.startPoint,jp.createdDate) from JobPost jp " +
            "join Dog d on jp.dog.dogId = d.dogId " +
            "where jp.member.id = :memberId and jp.deletedYn = false")
    Page<MyJobPostResponseDto> findListAppliedToTheJobPost(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select new com.doggyWalky.doggyWalky.jobpost.dto.response.JobPostDetailResponseDto(jp.member.id, mpi.profileImage, mpi.nickName, jp.id, jp.title, jp.defaultImage, jp.content, jp.status,jp.startPoint,jp.bcode,jp.createdDate,d.dogId,d.profileImage,d.name,d.dogSize) from JobPost jp " +
            "join MemberProfileInfo mpi on jp.member.id = mpi.member.id " +
            "join Dog d on jp.dog.dogId = d.dogId " +
            "where jp.deletedYn = false and jp.id = :jobPostId and mpi.deletedYn = false")
    Optional<JobPostDetailResponseDto> findJobPostDetailByPostId(@Param("jobPostId") Long jobPostId);
}

