package com.doggyWalky.doggyWalky.member.repository;

import com.doggyWalky.doggyWalky.member.entity.MemberProfileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberProfileInfoRepository extends JpaRepository<MemberProfileInfo, Long> {
}
