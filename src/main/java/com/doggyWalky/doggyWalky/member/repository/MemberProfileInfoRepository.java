package com.doggyWalky.doggyWalky.member.repository;

import com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto;
import com.doggyWalky.doggyWalky.member.entity.MemberProfileInfo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemberProfileInfoRepository extends JpaRepository<MemberProfileInfo, Long> {

    @Query("select new com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto(m.member.id, m.nickName, m.description, m.createdDate, m.updatedDate) " +
            "from MemberProfileInfo m where m.member.id= :memberId and m.deletedYn = :deletedYn")
    List<MemberProfileResponseDto> findMemberProfiles(@Param("memberId") Long memberId,@Param("deletedYn") boolean deletedYn);
}

