package com.doggyWalky.doggyWalky.oauth.service;


import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.exception.ApplicationException;
import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.member.dto.response.MemberSimpleResponseDto;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.entity.MemberProfileInfo;
import com.doggyWalky.doggyWalky.member.entity.MemberSecretInfo;
import com.doggyWalky.doggyWalky.member.entity.Role;
import com.doggyWalky.doggyWalky.member.repository.MemberProfileInfoRepository;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import com.doggyWalky.doggyWalky.member.repository.MemberSecretInfoRepository;
import com.doggyWalky.doggyWalky.member.repository.RoleRepository;
import com.doggyWalky.doggyWalky.oauth.domain.NaverOauth;
import com.doggyWalky.doggyWalky.oauth.dto.NaverOauthToken;
import com.doggyWalky.doggyWalky.oauth.dto.NaverUser;
import com.doggyWalky.doggyWalky.security.jwt.HmacAndBase64;
import com.doggyWalky.doggyWalky.security.jwt.RefreshTokenProvider;
import com.doggyWalky.doggyWalky.security.jwt.SymmetricCrypto;
import com.doggyWalky.doggyWalky.security.jwt.TokenProvider;
import com.doggyWalky.doggyWalky.security.redis.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static com.doggyWalky.doggyWalky.constant.ConstantPool.*;
import static com.doggyWalky.doggyWalky.exception.ErrorCode.INVALID_SOCIAL_LOGIN_TYPE;

@Service
@RequiredArgsConstructor
public class OauthService {
    private final NaverOauth naverOauth;

    private final MemberRepository memberRepository;

    private final MemberProfileInfoRepository memberProfileRepository;

    private final MemberSecretInfoRepository memberSecretRepository;

    private final RedisService redisService;

    private final SymmetricCrypto symmetricCrypto;

    private final HmacAndBase64 hmacAndBase64;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final RoleRepository roleRepository;



    @Transactional
    public MemberSimpleResponseDto oauthLogin(ConstantPool.SocialLoginType socialLoginType, String code , HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                // 회원가입 처리
                Member findMember;
                Optional<Member> memberOptional = memberRepository.findByEmail(symmetricCrypto.encrypt(naverUser.getEmail()));
                if (memberOptional.isEmpty()) {
                    System.out.println("신규 회원");
                    Role role = roleRepository.findByRoleName(Role.RoleName.USER).orElseThrow(() -> new ApplicationException(ErrorCode.AUTHORITY_NOT_FOUND));
                    Member member = new Member(symmetricCrypto.encrypt(naverUser.getEmail()),naverUser.getName(),role);
                    findMember = memberRepository.save(member);

                    MemberProfileInfo memberProfile = new MemberProfileInfo(member);
                    memberProfileRepository.save(memberProfile);

                    MemberSecretInfo memberSecret = new MemberSecretInfo(member,naverUser.getMobile());
                    memberSecretRepository.save(memberSecret);



                    System.out.println("회원 비교 :" + (member==findMember));
                    System.out.println("회원 이메일(실제) :" +naverUser.getEmail());
                    System.out.println("회원 이메일(DB 암호화) :" +findMember.getEmail());
                    System.out.println("회원 이메일(DB 복호화) :" +symmetricCrypto.decrypt(findMember.getEmail()));
                } else {
                    findMember = memberOptional.get();
                }

                System.out.println("해당 회원 저장 여부 :"+memberRepository.findByEmail(symmetricCrypto.encrypt(naverUser.getEmail())).get().getEmail());

                // JWT 토큰 발급
                // ----------------------------------------------------------
                String ipAddress = request.getRemoteAddr();

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(symmetricCrypto.encrypt(naverUser.getEmail()),null);

                // authenticationToken을 이용해서 Authentication 객체를 생성하려고 authentication 메소드가 실행이 될 때
                // CustomUserDetailsService의 loadUserByUsername 메소드가 실행된다.
                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

                // createToken 메소드를 통해서 JWT Token 생성
                String jwt = tokenProvider.createToken(authentication);
                String refresh = refreshTokenProvider.createToken(authentication, ipAddress);

                response.addHeader(AUTHORIZATION_HEADER, "Bearer "+jwt);
                response.addHeader(REFRESH_HEADER, "Bearer "+refresh);

                // REDIS에 Refresh Token 저장
                try {
                    // "refresh:암호화된IP_pk"을 key값으로 refreshToken을 Redis에 저장한다.
                    redisService.setRefreshToken("refresh:" + hmacAndBase64.crypt(ipAddress, "HmacSHA512")
                            + "_" + authentication.getName(), refresh);
                } catch (UnsupportedEncodingException | NoSuchAlgorithmException | InvalidKeyException e) {
                    throw new ApplicationException(ErrorCode.CRYPT_ERROR);
                }

                return new MemberSimpleResponseDto(findMember.getId());



            }
            default -> {
                throw new ApplicationException(INVALID_SOCIAL_LOGIN_TYPE);
            }
        }
    }
}
