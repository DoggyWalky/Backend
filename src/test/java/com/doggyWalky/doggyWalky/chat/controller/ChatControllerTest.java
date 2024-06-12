package com.doggyWalky.doggyWalky.chat.controller;

import com.doggyWalky.doggyWalky.chat.dto.request.ContactRequestDto;
import com.doggyWalky.doggyWalky.chat.dto.request.QuitRequestDto;
import com.doggyWalky.doggyWalky.chat.dto.request.UnvisibleRequestDto;
import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class ChatControllerTest extends RestDocsTestSupport {

    @Autowired
    private UserDetailsService userDetailsService;

    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";


    @DisplayName("채팅방 생성 테스트")
    @Test
    public void contact_200() throws Exception {

        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        ContactRequestDto dto = new ContactRequestDto(24L, 3L);

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String contactRequestDto = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/api/contact")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(contactRequestDto))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("receiverId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅을 시도할 사용자의 고유 번호")
                                        .attributes(constraints("채팅방을 생성하려는 게시글 작성자의 고유 번호여야합니다.")),
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅방을 생성할 게시글의 고유 번호")
                        )
                ));
    }

    @DisplayName("채팅방 숨김 테스트")
    @Test
    public void unvisible_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        UnvisibleRequestDto dto = new UnvisibleRequestDto(24L, 3L);

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String unvisibleRequestDto = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/api/unvisible")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unvisibleRequestDto))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("receiverId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅방의 상대방 사용자의 고유 번호")
                                        .attributes(constraints("현재 채팅방의 상대방 사용자의 고유 번호여야 합니다.")),
                                fieldWithPath("chatRoomId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("현재 채팅방의 고유 번호여야 합니다.")
                        )
                ));
    }

    @DisplayName("채팅방 나가기 테스트")
    @Test
    public void quit_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        QuitRequestDto dto = new QuitRequestDto(24L, 3L);

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String quitRequestDto = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/api/quit")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(quitRequestDto))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("receiverId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("채팅방의 상대방 사용자의 고유 번호")
                                        .attributes(constraints("현재 채팅방의 상대방 사용자의 고유 번호여야 합니다.")),
                                fieldWithPath("chatRoomId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("현재 채팅방의 고유 번호여야 합니다.")
                        )
                ));
    }


}