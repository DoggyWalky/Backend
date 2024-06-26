package com.doggyWalky.doggyWalky.security.jwt;

import com.doggyWalky.doggyWalky.exception.ErrorCode;
import com.doggyWalky.doggyWalky.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 유효한 자격증명을 제공하지 않고 접근하려할 때 401 Unauthorized 에러를 리턴할 JwtAuthenticationEntryPoint 클래스
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = mapper.writeValueAsString(ErrorResponse.of(ErrorCode.UNAUTHORIZED.name()));

        response.setContentType("application/json");

        //유효한 토큰이 아닐 시 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(jsonInString);
    }
}
