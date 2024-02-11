package com.doggyWalky.doggyWalky.config;

import com.doggyWalky.doggyWalky.security.jwt.HmacAndBase64;
import com.doggyWalky.doggyWalky.security.jwt.JwtFilter;
import com.doggyWalky.doggyWalky.security.jwt.RefreshTokenProvider;
import com.doggyWalky.doggyWalky.security.jwt.TokenProvider;
import com.doggyWalky.doggyWalky.security.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// SecurityConfigurerAdapter를 extends하고 TokenProvider를 주입받아서 JwtFilter를 통해 Security 로직에 필터를 등록한다.
@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    private final RefreshTokenProvider refreshTokenProvider;

    private final RedisService redisService;

    private final HmacAndBase64 hmacAndBase64;

    @Override
    public void configure(HttpSecurity http) {
        http.addFilterBefore(
                new JwtFilter(tokenProvider,refreshTokenProvider,redisService, hmacAndBase64),
                UsernamePasswordAuthenticationFilter.class
        );
    }
}
