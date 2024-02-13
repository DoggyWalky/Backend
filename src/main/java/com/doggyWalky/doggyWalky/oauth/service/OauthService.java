package com.doggyWalky.doggyWalky.oauth.service;


import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import com.doggyWalky.doggyWalky.oauth.domain.NaverOauth;
import com.doggyWalky.doggyWalky.oauth.dto.NaverOauthToken;
import com.doggyWalky.doggyWalky.oauth.dto.NaverUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.doggyWalky.doggyWalky.constant.ConstantPool.*;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final NaverOauth naverOauth;
    private final HttpServletResponse response;

    private final MemberRepository memberRepository;


    public void request(SocialLoginType socialLoginType) throws IOException {
        String redirectURL = switch (socialLoginType) {
            case NAVER -> {
                yield naverOauth.getOauthRedirectURL();
            }
            default -> {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        };

        response.sendRedirect(redirectURL);
    }

    public NaverUser oauthLogin(ConstantPool.SocialLoginType socialLoginType, String code) throws IOException {
        switch (socialLoginType) {
            case NAVER -> {
                // 네이버로 일회성 코드를 보내 액세스 토큰이 담긴 응답객체를 받아온다
                ResponseEntity<String> accessTokenResponse = naverOauth.requestAccessToken(code);
                // 응답 객체가 JSON 형식으로 되어 있으므로, 이를 역직렬화해서 자바 객체에 담는다.
                NaverOauthToken oauthToken = naverOauth.getAccessToken(accessTokenResponse);
                // 액세스 토큰을 다시 네이버로 보내 네이버에 저장된 사용자 정보가 담긴 응답 객체를 받아온다.
                ResponseEntity<String> userInfoResponse = naverOauth.requestUserInfo(oauthToken);
                // 다시 JSON 형식의 응답 객체를 자바 객체로 역직렬화한다.
                NaverUser naverUser = naverOauth.getUserInfo(userInfoResponse);

                // DB에 회원 이메일이 등록 되어있는지 확인 후 있으면 회원가입 처리 및 토큰 발급, 없으면 그냥 토큰 발급
                if (memberRepository.findByEmail(naverUser.getEmail()).isEmpty()) {

                }
            }
            default -> {
                throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
            }
        }

        return null;
    }
}
