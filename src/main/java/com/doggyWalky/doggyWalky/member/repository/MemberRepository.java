package com.doggyWalky.doggyWalky.member.repository;

import com.doggyWalky.doggyWalky.member.entity.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByEmail(@Param("email") String email);
}
