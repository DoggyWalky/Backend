package com.doggyWalky.doggyWalky.member.controller;

import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import com.doggyWalky.doggyWalky.security.jwt.HmacAndBase64;
import com.doggyWalky.doggyWalky.security.redis.TokenStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final TokenStorageService redisService;

    private final HmacAndBase64 hmacAndBase64;

    private final MemberService memberService;

    /**
     * 로그아웃 시 토큰 제거
     */
    @GetMapping("/removeToken")
    public ResponseEntity removeToken(Principal principal, HttpServletRequest request) {
        try {
            // Refresh 토큰을 Redis에서 제거하는 작업
            redisService.removeRefreshToken("refresh:" + hmacAndBase64.crypt(request.getRemoteAddr(), "HmacSHA512") + "_" + principal.getName());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ApplicationException(ErrorCode.CRYPT_ERROR);
        }

        return new ResponseEntity(principal.getName() + " 삭제완료", HttpStatus.OK);
    }

    /**
     * 개인 회원정보 조회
     */
    @GetMapping("/members")
    public ResponseEntity getMember(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        List<MemberProfileResponseDto> memberProfiles = memberService.getMemberProfiles(memberId);
        return new ResponseEntity(memberProfiles, HttpStatus.OK);
    }
}
