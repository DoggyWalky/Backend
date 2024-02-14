package com.doggyWalky.doggyWalky.oauth.controller;

import com.doggyWalky.doggyWalky.member.dto.response.MemberSimpleResponseDto;
import com.doggyWalky.doggyWalky.oauth.service.OauthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.doggyWalky.doggyWalky.constant.ConstantPool.*;

@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    /**
     * 소셜 로그인 요청
     */
    @GetMapping("/auth/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String type)  throws IOException {
        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        oauthService.request(socialLoginType);
    }

    /**
     * 소셜 로그인 요청에 대한 응답 처리(로그인, 회원가입 처리)
     */
    @GetMapping("/auth/{socialLoginType}/callback")
    public ResponseEntity callback(
            @PathVariable(name="socialLoginType") String type,
            @RequestParam(name="code") String code,
            HttpServletRequest request) throws IOException {

        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :"+ code);
        HttpHeaders httpHeaders = new HttpHeaders();
        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        Long memberId=Long.parseLong(oauthService.oauthLogin(socialLoginType,code,httpHeaders,request));
        return new ResponseEntity<>(new MemberSimpleResponseDto(memberId),httpHeaders ,HttpStatus.OK);
    }

}
