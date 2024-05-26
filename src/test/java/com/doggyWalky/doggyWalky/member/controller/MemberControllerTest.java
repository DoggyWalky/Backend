package com.doggyWalky.doggyWalky.member.controller;

import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.security.redis.RedisService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.transaction.annotation.Transactional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
class MemberControllerTest extends RestDocsTestSupport {


    @MockBean
    protected RedisService redisService;

    @Autowired
    private UserDetailsService userDetailsService;

    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @Test
    public void removetoken_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        System.out.println("getUserName() :" + userDetails.getUsername());
        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(Mockito.anyString())).thenReturn(null);

        // when
        mockMvc.perform(get("/removeToken")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(24L))
                .andDo(restDocs.document(
                        responseFields( // 응답 필드 추가
                                fieldWithPath("memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호")
                                        .attributes(constraints("멤버 PK는 고유해야합니다."))
                        )
                ));

        // then
        // Mock 객체 호출 확인 - verify를 이용하여 redisService의 removeRefreshToken(refreshTokenKey)가 실제로 호출되었는지 확인합니다.
        // 실제로는 IP 주소가 계속 바뀌므로 IP 주소는 동일하다는 가정하에 removeRefreshToken이 호출되었는지에 대한 테스트를 진행하였습니다.
        Mockito.verify(redisService).removeRefreshToken(anyString());
        // Mock 객체 설정에 의해 Null 값 반환 확인하는 작업입니다.
        assertNull(redisService.getRefreshToken("refreshTokenKey"));
    }
}