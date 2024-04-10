package com.doggyWalky.doggyWalky.apply.repository;

import com.doggyWalky.doggyWalky.apply.dto.response.ApplyResponseDto;
import com.doggyWalky.doggyWalky.apply.entity.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
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
            "where a.jobPost.id = : jobPostId and a.owner.id =: ownerId and a.owner.deletedYn = false")
    List<ApplyResponseDto> findListAppliedToTheJobPost(@Param("jobPostId") Long jobPostId, @Param("ownerId") Long ownerId);
}