package com.doggyWalky.doggyWalky.member.controller;

import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.request.FileRequestDto;
import com.doggyWalky.doggyWalky.file.repository.FileRepository;
import com.doggyWalky.doggyWalky.file.service.FileService;
import com.doggyWalky.doggyWalky.member.dto.request.MemberPatchProfileDto;
import com.doggyWalky.doggyWalky.member.dto.response.MemberProfileResponseDto;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.repository.MemberProfileInfoRepository;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import com.doggyWalky.doggyWalky.member.repository.MemberSecretInfoRepository;
import com.doggyWalky.doggyWalky.security.jwt.SymmetricCrypto;
import com.doggyWalky.doggyWalky.security.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class MemberControllerTest extends RestDocsTestSupport {


    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberProfileInfoRepository memberProfileInfoRepository;

    @Autowired
    MemberSecretInfoRepository memberSecretInfoRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    protected RedisService redisService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FileService fileService;


    @Autowired
    private SymmetricCrypto crypto;



    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("로그아웃 테스트")
    @Test
    public void removetoken_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(Mockito.anyString())).thenReturn(null);

        // when
        mockMvc.perform(get("/api/remove-token")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(24L))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호")
                        )
                ));

        // then
        // Mock 객체 호출 확인 - verify를 이용하여 redisService의 removeRefreshToken(refreshTokenKey)가 실제로 호출되었는지 확인합니다.
        // 실제로는 IP 주소가 계속 바뀌므로 IP 주소는 동일하다는 가정하에 removeRefreshToken이 호출되었는지에 대한 테스트를 진행하였습니다.
        Mockito.verify(redisService).removeRefreshToken(anyString());
        // Mock 객체 설정에 의해 Null 값 반환 확인하는 작업입니다.
        assertNull(redisService.getRefreshToken("refreshTokenKey"));
    }

    @DisplayName("회원 탈퇴")
    @Test
    public void updatememberdelete_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // Mock 객체 설정 - redisService 목 객체에 getRefreshToken() 메소드를 호출할 시 Null 값을 반환하도록 설정
        Mockito.when(redisService.getRefreshToken(anyString())).thenReturn(null);

        Member member = memberRepository.findById(Long.parseLong(userDetails.getUsername())).get();


        // when
        mockMvc.perform(delete("/api/members")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("탈퇴된 멤버 고유 번호")
                        )
                ));

        // then

        // 회원, 프로필, 상세에 대해 softDelete 설정 했는지에 대한 테스트
        Member findMember = memberRepository.findById(member.getId()).get();
        assertEquals(true,findMember.isDeletedYn());
        assertEquals(true,memberProfileInfoRepository.findByMemberId(member.getId(),false).isEmpty());
        assertEquals(true,memberSecretInfoRepository.findByMemberId(member.getId(),false).isEmpty());

        // Redis에 토큰 제거 테스트
        Mockito.verify(redisService).removeRefreshToken(anyString());
        assertNull(redisService.getRefreshToken(anyString()));

        //  회원 프로필 이미지 삭제했는지 확인 테스트
        assertEquals(true,fileService.findFilesByTableInfo(FileRequestDto.create(TableName.MEMBER_PROFILE_INFO, memberProfileInfoRepository.findByMemberId(member.getId(), true).get().getId()), false).isEmpty());


        // 회원이 등록한 게시글 및 게시글 관련 정보, 강아지, 산책 관련 모든 내용 삭제 테스트


    }

    @DisplayName("회원 프로필 수정 테스트")
    @Test
    public void modifymemberprofile_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        MemberPatchProfileDto profile = new MemberPatchProfileDto("수정할닉네임", "테스트를 위한 프로필 수정입니다. 참고해주시면 감사하겠습니다", 42L, "https://doggywalky-bucket.s3.ap-northeast-2.amazonaws.com/basic_user_image.jpg");
        String profileImage = fileRepository.findById(42L).get().getPath();

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String profileJson = objectMapper.writeValueAsString(profile);

        // when & then
        mockMvc.perform(patch("/api/members/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(APPLICATION_JSON)
                        .content(profileJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(userDetails.getUsername()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields( // 요청 필드 추가
                                fieldWithPath("nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("수정하고 싶은 닉네임")
                                        .attributes(constraints("닉네임 형식은 한글,영문 대소문자, 숫자로 이루어지며 최소 5글자 이상 최대 15글자 이하입니다.")),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("수정하고 싶은 자기 소개글")
                                        .attributes(constraints("자기 소개글은 최소 20글자 이상 최대 500글자 이하입니다.")),
                                fieldWithPath("fileId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("수정하고 싶은 파일 pk")
                                        .attributes(constraints("NULL일 경우 기존 이미지 사용")),
                                fieldWithPath("profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("수정하고 싶은 프로필 이미지 주소")
                                        .attributes(constraints("NULL일 경우 사용자 기본 이미지 사용"))
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("수정된 멤버 고유 번호")
                        )
                ));

        // 변경된 이후의 회원 프로필을 DB에서 뽑아옵니다.
        MemberProfileResponseDto modifyProfile = memberProfileInfoRepository.findMemberProfiles(Long.parseLong(userDetails.getUsername()), false).get(0);


        // 비교하기
        assertEquals(profile.getNickName(), modifyProfile.getNickName());
        assertEquals(profile.getDescription(), modifyProfile.getDescription());
        assertEquals(profileImage, modifyProfile.getProfileImage());
    }

    @DisplayName("회원 프로필 조회 테스트")
    @Test
    public void getmemberprofile_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // when
        // pk가 24L인 사용자의 프로필을 조회합니다. 헤더의 토큰은 해당 pk의 사용자 토큰입니다.
        List<MemberProfileResponseDto> memberProfiles = memberProfileInfoRepository.findMemberProfiles(Long.parseLong(userDetails.getUsername()), false);
        MemberProfileResponseDto memberProfile = memberProfiles.get(0);


        // then
        // jsonPath를 이용하여 컨트롤러가 주는 HTTP 응답 바디에 접근하여 값을 비교하는 코드입니다.
        mockMvc.perform(get("/api/members/profile")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(memberProfile.getMemberId()))
                .andExpect(jsonPath("$[0].nickName").value(memberProfile.getNickName()))
                .andExpect(jsonPath("$[0].description").value(memberProfile.getDescription()))
                .andExpect(jsonPath("$[0].createdAt").value(memberProfile.getCreatedAt()))
                .andExpect(jsonPath("$[0].updatedAt").value(memberProfile.getUpdatedAt()))
                .andExpect(jsonPath("$[0].profileImage").value(memberProfile.getProfileImage()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호"),
                                fieldWithPath("[].nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임"),
                                fieldWithPath("[].description")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 자기소개글"),
                                fieldWithPath("[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 생성 날짜"),
                                fieldWithPath("[].updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 수정 날짜"),
                                fieldWithPath("[].profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필 이미지 주소")
                        )
                ));

    }

    @DisplayName("타회원 프로필 조회 테스트")
    @Test
    public void getothermemberprofile_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // when
        // pk가 24L인 사용자의 프로필을 조회합니다. 헤더의 토큰은 해당 pk의 사용자 토큰입니다.
        List<MemberProfileResponseDto> memberProfiles = memberProfileInfoRepository.findMemberProfiles(20L, false);
        MemberProfileResponseDto memberProfile = memberProfiles.get(0);


        // then
        // jsonPath를 이용하여 컨트롤러가 주는 HTTP 응답 바디에 접근하여 값을 비교하는 코드입니다.
        mockMvc.perform(get("/api/members/{user-id}/profile",20L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberId").value(memberProfile.getMemberId()))
                .andExpect(jsonPath("$[0].nickName").value(memberProfile.getNickName()))
                .andExpect(jsonPath("$[0].description").value(memberProfile.getDescription()))
                .andExpect(jsonPath("$[0].createdAt").value(memberProfile.getCreatedAt()))
                .andExpect(jsonPath("$[0].updatedAt").value(memberProfile.getUpdatedAt()))
                .andExpect(jsonPath("$[0].profileImage").value(memberProfile.getProfileImage()))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("멤버 고유 번호"),
                                fieldWithPath("[].nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 닉네임"),
                                fieldWithPath("[].description")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 자기소개글"),
                                fieldWithPath("[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 생성 날짜"),
                                fieldWithPath("[].updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 수정 날짜"),
                                fieldWithPath("[].profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("멤버 프로필 이미지 주소")
                        )
                ));

    }
}