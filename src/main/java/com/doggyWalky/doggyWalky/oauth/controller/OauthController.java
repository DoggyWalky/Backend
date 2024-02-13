package com.doggyWalky.doggyWalky.oauth.controller;

import com.doggyWalky.doggyWalky.oauth.dto.NaverUser;
import com.doggyWalky.doggyWalky.oauth.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

    @GetMapping("/auth/{socialLoginType}")
    public void socialLoginRedirect(@PathVariable(name="socialLoginType") String type)  throws IOException {
        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        oauthService.request(socialLoginType);
    }

    @GetMapping("/auth/{socialLoginType}/callback")
    public ResponseEntity callback(
            @PathVariable(name="socialLoginType") String type,
            @RequestParam(name="code") String code,
            @RequestParam(name="state") String state) throws IOException {

        System.out.println(">> 소셜 로그인 API 서버로부터 받은 code :"+ code);
        SocialLoginType socialLoginType= SocialLoginType.valueOf(type.toUpperCase());
        NaverUser user=oauthService.oauthLogin(socialLoginType,code);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

}
