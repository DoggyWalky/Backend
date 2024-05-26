package com.doggyWalky.doggyWalky.member.controller;

import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.member.dto.request.MemberPatchProfileDto;
import com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto;
import com.doggyWalky.doggyWalky.member.dto.response.MemberSimpleResponseDto;
import com.doggyWalky.doggyWalky.member.service.MemberService;
import com.doggyWalky.doggyWalky.security.jwt.HmacAndBase64;
import com.doggyWalky.doggyWalky.security.redis.TokenStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final TokenStorageService redisService;

    private final HmacAndBase64 hmacAndBase64;

    private final MemberService memberService;

    /**
     * 로그아웃 시 토큰 제거
     */
    @GetMapping("/remove-token")
    public ResponseEntity removeToken(Principal principal, HttpServletRequest request) {
        try {
            // Refresh 토큰을 Redis에서 제거하는 작업
            redisService.removeRefreshToken("refresh:" + hmacAndBase64.crypt(request.getRemoteAddr(), "HmacSHA512") + "_" + principal.getName());
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new ApplicationException(ErrorCode.CRYPT_ERROR);
        }

        return new ResponseEntity(new MemberSimpleResponseDto(Long.parseLong(principal.getName())), HttpStatus.OK);
    }

    /**
     * 개인 회원정보 조회
     */
    @GetMapping("/members/profile")
    public ResponseEntity getMember(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        List<MemberProfileResponseDto> memberProfiles = memberService.getMemberProfiles(memberId);
        System.out.println(memberProfiles);
        return new ResponseEntity(memberProfiles, HttpStatus.OK);
    }

    /**
     * 회원 프로필 수정
     */
    @PatchMapping("/members/profile")
    public ResponseEntity<MemberSimpleResponseDto> updateMemberProfile(@Valid @RequestBody MemberPatchProfileDto dto, BindingResult bindingResult, Principal principal) {
        // Validation 체크
        if (bindingResult.hasErrors()) {

            if (bindingResult.hasFieldErrors("nickName")) {
                throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_NICKNAME);
            }

            if (bindingResult.hasFieldErrors("description")) {
                throw new ApplicationException(ErrorCode.INCORRECT_FORMAT_DESCRIPTION);
            }

        }

        Long memberId = Long.parseLong(principal.getName());
        memberService.updateMemberProfile(memberId, dto);
        return new ResponseEntity<>(new MemberSimpleResponseDto(memberId), HttpStatus.OK);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/members")
    public ResponseEntity updateMemberDelete(Principal principal, HttpServletRequest request) {
        Long memberId = Long.parseLong(principal.getName());
        memberService.updateMemberDelete(memberId, request);

        // Todo: Access Token은 요청을 보낸 Axios API에서 쿠키를 삭제하도록 지시한다.
        return new ResponseEntity(new MemberSimpleResponseDto(memberId), HttpStatus.OK);
    }

    // Todo: 다른 사람 프로필 조회하는 API 작성하기


}

