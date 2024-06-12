package com.doggyWalky.doggyWalky.oauth.controller;

import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.member.dto.response.MemberSimpleResponseDto;
import com.doggyWalky.doggyWalky.oauth.service.OauthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.data.redis.connection.ReactiveStringCommands.BitOpCommand.perform;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Transactional
@DirtiesContext
class OauthControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    OauthService oauthService;

    @DisplayName("소셜 로그인 요청에 대한 응답 처리(로그인, 회원가입 처리) 테스트")
    @Test
    public void sociallogin_200() throws Exception {

        Mockito.when(oauthService.oauthLogin(eq(ConstantPool.SocialLoginType.NAVER),anyString(),any(),any())).thenReturn(new MemberSimpleResponseDto(1L));

        // when
        mockMvc.perform(get("/api/auth/{socialLoginType}/callback", "NAVER").param("code","code_example"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(1L))
                .andDo(MockMvcResultHandlers.print()) // 요청, 응답 출력
                .andDo(MockMvcRestDocumentation.document("{class-name}/{method-name}", // 문서 이름 설정
                        preprocessRequest(
                                modifyHeaders() // 헤더 내용 수정
                                        .remove("Content-Length")
                                        .remove("Host"),
                                prettyPrint()), // 한 줄로 출력되는 json에 pretty 포멧 적용
                        preprocessResponse(
                                modifyHeaders()
                                        .remove("Content-Length")
                                        .remove("X-Content-Type-Options")
                                        .remove("X-XSS-Protection")
                                        .remove("Cache-Control")
                                        .remove("Pragma")
                                        .remove("Expires")
                                        .remove("X-Frame-Options"),
                                prettyPrint()),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("회원가입된 멤버 고유 번호")
                        )
                ));
    }
}