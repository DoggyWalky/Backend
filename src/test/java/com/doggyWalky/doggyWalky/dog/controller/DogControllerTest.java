package com.doggyWalky.doggyWalky.dog.controller;

import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.dog.dto.OwnersDogResponse;
import com.doggyWalky.doggyWalky.dog.dto.ReadDogResponse;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogRequest;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogResponse;
import com.doggyWalky.doggyWalky.dog.service.DogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class DogControllerTest extends RestDocsTestSupport {

    @Autowired
    private UserDetailsService userDetailsService;


    @MockBean
    private DogService dogService;


    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("강아지 등록 테스트")
    @Test
    public void registerdog_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        RegisterDogRequest dogRequest = new RegisterDogRequest("3","귀여운 말티즈입니다.","보리","소형견");

        RegisterDogResponse dogResponse = new RegisterDogResponse(1L,"3","귀여운 말티즈입니다.","보리");

        Mockito.when(dogService.registerDog(anyLong(), any(RegisterDogRequest.class))).thenReturn(dogResponse);

        String dogRequestToJson = objectMapper.writeValueAsString(dogRequest);


        //when & then
        mockMvc.perform(post("/api/dog/register")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dogRequestToJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dogId").value(dogResponse.getDogId()))
                .andExpect(jsonPath("$.weight").value(dogResponse.getWeight()))
                .andExpect(jsonPath("$.description").value(dogResponse.getDescription()))
                .andExpect(jsonPath("$.name").value(dogResponse.getName()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("weight")
                                        .type(JsonFieldType.STRING)
                                        .description("강아지의 몸무게"),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("강아지의 소개글"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("강아지의 이름"),
                                fieldWithPath("kind")
                                        .type(JsonFieldType.STRING)
                                        .description("강아지의 분류")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("dogId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("등록된 강아지의 고유 번호"),
                                fieldWithPath("weight")
                                        .type(JsonFieldType.STRING)
                                        .description("등록된 강아지의 몸무게"),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("등록된 강아지의 소개글"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("등록된 강아지의 이름")
                        )
                ));

    }

    @DisplayName("강아지 PK를 이용한 강아지 조회 테스트")
    @Test
    public void finddogbyid_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        ReadDogResponse dogResponse = new ReadDogResponse(1L,"소형견","3","말티즈입니다.","보리", LocalDateTime.now());
        Mockito.when(dogService.findDogByDogId(anyLong())).thenReturn(dogResponse);

        //when & then
        mockMvc.perform(get("/api/dog/{dog-id}",dogResponse.getDogId())
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("dogId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("조회한 강아지의 고유 번호"),
                                fieldWithPath("kind")
                                        .type(JsonFieldType.STRING)
                                        .description("조회한 강아지의 분류"),
                                fieldWithPath("weight")
                                        .type(JsonFieldType.STRING)
                                        .description("조회한 강아지의 몸무게"),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("조회한 강아지의 소개글"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("조회한 강아지의 이름"),
                                fieldWithPath("insertDate")
                                        .type(JsonFieldType.VARIES)
                                        .description("조회한 강아지의 생성 일시")
                        )
                ));
    }

    @DisplayName("사용자 소유 강아지 목록 조회 테스트")
    @Test
    public void finddoglistbyownerid_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        OwnersDogResponse dogResponse1 = new OwnersDogResponse(1L,"DOG1","https://doggywalky-bucket.s3.ap-northeast-2.amazonaws.com/basic_dog_image.jpg");
        OwnersDogResponse dogResponse2 = new OwnersDogResponse(2L,"DOG2","https://doggywalky-bucket.s3.ap-northeast-2.amazonaws.com/basic_dog_image.jpg");
        OwnersDogResponse dogResponse3 = new OwnersDogResponse(3L,"DOG3","https://doggywalky-bucket.s3.ap-northeast-2.amazonaws.com/basic_dog_image.jpg");

        List<OwnersDogResponse> responseList = Arrays.asList(dogResponse1, dogResponse2, dogResponse3);

        Mockito.when(dogService.findDogByOwnerId(anyLong())).thenReturn(responseList);

        //when & then
        mockMvc.perform(get("/api/dog/owners/{owner-id}/dogs",1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(dogResponse1.getName()))
                .andExpect(jsonPath("$[1].name").value(dogResponse2.getName()))
                .andExpect(jsonPath("$[2].name").value((dogResponse3.getName())))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].dogId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("조회한 강아지의 고유 번호"),
                                fieldWithPath("[].name")
                                        .type(JsonFieldType.STRING)
                                        .description("조회한 강아지의 이름"),
                                fieldWithPath("[].fileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("조회한 강아지의 이미지 주소")
                        )
                ));
    }

    @DisplayName("강아지 정보 수정 테스트")
    @Test
    public void updatedog_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        RegisterDogRequest dogRequest = new RegisterDogRequest("2","말티즈입니다.","보리","소형견");
        String dogRequestToJson = objectMapper.writeValueAsString(dogRequest);

        Mockito.doNothing().when(dogService).updateDog(anyLong(), any(RegisterDogRequest.class));

        //when & then
        mockMvc.perform(patch("/api/dog/{dog-id}",1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dogRequestToJson))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("weight")
                                        .type(JsonFieldType.STRING)
                                        .description("수정할 강아지의 몸무게"),
                                fieldWithPath("description")
                                        .type(JsonFieldType.STRING)
                                        .description("수정할 강아지의 소개글"),
                                fieldWithPath("name")
                                        .type(JsonFieldType.STRING)
                                        .description("수정할 강아지의 이름"),
                                fieldWithPath("kind")
                                        .type(JsonFieldType.STRING)
                                        .description("수정할 강아지의 분류")
                        )
                ));
    }

    @DisplayName("강아지 삭제 테스트")
    @Test
    public void deletedog_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        Mockito.doNothing().when(dogService).deleteDog(anyLong());

        //when & then
        mockMvc.perform(delete("/api/dog/{dog-id}",1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        )
                ));
    }
}