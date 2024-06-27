package com.doggyWalky.doggyWalky.apply.repository;

import com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.dto.response.MyApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.entity.Apply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    @Query("select a from Apply a where a.jobPost.id = :jobPostId and a.worker.id = :workerId")
    Optional<Apply> findDuplicateApply(@Param("jobPostId") Long jobPostId, @Param("workerId") Long workerId);

    @Query("select new com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto(a.id, a.jobPost.id, a.owner.id, a.worker.id, a.worker.name, mpi.nickName, mpi.description, mpi.profileImage, a.status, a.createdDate, a.updatedDate) " +
            "from Apply a " +
            "join MemberProfileInfo mpi on a.worker.id = mpi.member.id and mpi.deletedYn=false " +
            "where a.jobPost.id = :jobPostId and a.owner.id = :ownerId and a.owner.deletedYn = false")
    Page<ApplyResponseDto> findListAppliedToTheJobPost(@Param("jobPostId") Long jobPostId, @Param("ownerId") Long ownerId, Pageable pageable);

    @Query("select a from Apply a where a.id = :applyId and a.owner.id = :ownerId")
    Optional<Apply> findByOwner(@Param("applyId") Long applyId, @Param("ownerId") Long ownerId);

    @Query("select a from Apply a where a.jobPost.id = :jobPostId and a.status = 'ACCEPT'")
    Optional<Apply> findAcceptedApplyByJobPostId(@Param("jobPostId") Long jobPostId);

    @Query("select new com.doggyWalky.doggyWalky.apply.dto.response.MyApplyResponseDto(jp.member.id, mpi.nickName, mpi.profileImage, jp.id, jp.title, jp.defaultImage, jp.status, d.dogId, d.kind, a.id, a.status) from Apply a " +
            "join JobPost jp on jp.id = a.jobPost.id " +
            "join Dog d on d.dogId = jp.dog.dogId " +
            "join MemberProfileInfo mpi on mpi.member.id = jp.member.id " +
            "where a.worker.id = :memberId and a.worker.deletedYn = false and jp.deletedYn = false and mpi.deletedYn = false")
    Page<MyApplyResponseDto> getMyApplyList(@Param("memberId") Long memberId, Pageable pageable);

    @Modifying
    @Query("delete from Apply a where a.jobPost.id = :jobPostId")
    void deleteByJobPostId(@Param("jobPostId") Long jobPostId);
}
