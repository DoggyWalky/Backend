package com.doggyWalky.doggyWalky.member.service;

import com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto;
import com.doggyWalky.doggyWalky.member.repository.MemberProfileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberProfileInfoRepository memberProfileInfoRepository;

    public List<MemberProfileResponseDto> getMemberProfiles(Long memberId) {
        List<MemberProfileResponseDto> memberProfiles = memberProfileInfoRepository.findMemberProfiles(memberId, false);
        return memberProfiles;
    }
}
