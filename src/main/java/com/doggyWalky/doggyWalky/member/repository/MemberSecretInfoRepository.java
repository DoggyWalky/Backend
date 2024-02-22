package com.doggyWalky.doggyWalky.member.repository;

import com.doggyWalky.doggyWalky.member.entity.MemberSecretInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberSecretInfoRepository extends JpaRepository<MemberSecretInfo,Long> {

    @Query("select m from MemberSecretInfo m where m.member.id= :memberId and m.deletedYn = :deletedYn")
    Optional<MemberSecretInfo> findByMemberId(@Param("memberId")Long memberId, @Param("deleteYn") boolean deletedYn);
}
