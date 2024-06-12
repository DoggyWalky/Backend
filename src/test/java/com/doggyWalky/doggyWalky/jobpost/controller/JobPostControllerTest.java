package com.doggyWalky.doggyWalky.jobpost.controller;

import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.jobpost.dto.*;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.entity.WalkingProcessStatus;
import com.doggyWalky.doggyWalky.jobpost.service.JobPostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class JobPostControllerTest extends RestDocsTestSupport {

    @MockBean
    private JobPostService jobPostService;

    @Autowired
    private UserDetailsService userDetailsService;

    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("게시글 등록 테스트")
    @Test
    public void registerpost_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        JobPostRegisterRequest jobPostRegisterRequest = new JobPostRegisterRequest("게시글 제목","게시글 내용", Status.WAITING,"출발지","목적지",3L);
        String jobPostToJson = objectMapper.writeValueAsString(jobPostRegisterRequest);

        MockMultipartFile jobPost = new MockMultipartFile("jobPost", "", "application/json", jobPostToJson.getBytes());
        MockMultipartFile image1 = new MockMultipartFile("images", "image1.jpg", "image/jpeg", "test image 1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("images", "image2.jpg", "image/jpeg", "test image 2".getBytes());

        JobPostRegisterResponse response = new JobPostRegisterResponse(1L,1L,jobPostRegisterRequest.getTitle(),
                jobPostRegisterRequest.getContent(),jobPostRegisterRequest.getStatus().getEnStatus(),jobPostRegisterRequest.getStartPoint(),jobPostRegisterRequest.getEndPoint()
        , jobPostRegisterRequest.getDogId(), Arrays.asList(image1.getName(), image2.getName()));

        Mockito.when(jobPostService.register(any(Long.class), any(JobPostRegisterRequest.class), any(List.class))).thenReturn(response);

        //when & then
        mockMvc.perform(multipart("/api/job-post/register")
                        .file(jobPost)
                        .file(image1)
                        .file(image2)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestParts(partWithName("jobPost").description("등록할 게시글 정보"),
                                partWithName("images").description("업로드할 게시글 이미지 파일 목록(form-data 형태의 파일 목록)")) // 요청에 등록할 게시글 정보 및 파일 목록 추가
                        ,
                        requestPartFields("jobPost",
                                fieldWithPath("title").type(JsonFieldType.STRING).description("등록할 게시글의 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("등록할 게시글의 내용"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("등록할 게시글의 구인 상태"),
                                fieldWithPath("startPoint").type(JsonFieldType.STRING).description("등록할 게시글의 출발지"),
                                fieldWithPath("endPoint").type(JsonFieldType.STRING).description("등록할 게시글의 목적지"),
                                fieldWithPath("dogId").type(JsonFieldType.NUMBER).description("등록할 게시글에서 산책시킬 강아지의 고유 번호")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("등록한 게시글의 고유 번호"),
                                fieldWithPath("memberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("등록한 게시글의 작성자의 고유 번호"),
                                fieldWithPath("title")
                                        .type(JsonFieldType.STRING)
                                        .description("등록한 게시글의 제목"),
                                fieldWithPath("content")
                                        .type(JsonFieldType.STRING)
                                        .description("등록한 게시글의 내용"),
                                fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("등록한 게시글의 구인 상태"),
                                fieldWithPath("startPoint")
                                        .type(JsonFieldType.STRING)
                                        .description("등록한 게시글의 출발지"),
                                fieldWithPath("endPoint")
                                        .type(JsonFieldType.STRING)
                                        .description("등록한 게시글의 목적지"),
                                fieldWithPath("dogId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("등록한 게시글의 산책시킬 강아지의 고유 번호"),
                                fieldWithPath("images")
                                        .type(JsonFieldType.VARIES)
                                        .description("등록한 게시글의 파일 이미지")
                        )
                ));
    }

    @DisplayName("게시글 검색조건 조회 테스트")
    @Test
    public void searchpostwithcondition_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        JobPost jobPost1 = new JobPost("Title1", "Content1", Status.WAITING, WalkingProcessStatus.PREWALK,"StartPoint1", "EndPoint1", 1L,false);
        JobPost jobPost2 = new JobPost("Title2", "Content2", Status.WAITING, WalkingProcessStatus.PREWALK,"StartPoint2", "EndPoint2", 2L,false);
        List<JobPost> jobPosts = Arrays.asList(jobPost1, jobPost2);

        Mockito.when(jobPostService.searchJobPosts(Mockito.any(JobPostSearchCriteria.class)))
                .thenReturn(jobPosts);

        mockMvc.perform(get("/api/job-post/search")
                        .param("title", "Title")
                        .param("status", "WAITING")
                        .param("startPoint", "StartPoint")
                        .contentType(MediaType.APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).optional().description("게시글 ID"),
                                fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시글 제목"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("게시글 내용"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("게시글 상태"),
                                fieldWithPath("[].startPoint").type(JsonFieldType.STRING).description("출발지"),
                                fieldWithPath("[].endPoint").type(JsonFieldType.STRING).description("목적지"),
                                fieldWithPath("[].dogId").type(JsonFieldType.NUMBER).description("강아지 ID"),
                                fieldWithPath("[].createdDate").type(JsonFieldType.VARIES).optional().description("게시글 생성 일시"),
                                fieldWithPath("[].updatedDate").type(JsonFieldType.VARIES).optional().description("게시글 수정 일시"),
                                fieldWithPath("[].member").type(JsonFieldType.VARIES).optional().description("게시글 작성자"),
                                fieldWithPath("[].walkingProcessStatus").type(JsonFieldType.STRING).description("게시글 산책 진행 상태"),
                                fieldWithPath("[].defaultImage").type(JsonFieldType.STRING).optional().description("게시글 이미지 주소"),
                                fieldWithPath("[].deletedYn").type(JsonFieldType.BOOLEAN).description("게시글 삭제 여부")
                        )));
    }





    @DisplayName("게시글의 산책 종료 설정 테스트")
    @Test
    public void walkcomplete_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        JobPostSimpleResponseDto responseDto = new JobPostSimpleResponseDto(1L);

        Mockito.when(jobPostService.setWalkingComplete(anyLong(), anyLong())).thenReturn(responseDto);

        //when & then
        mockMvc.perform(post("/api/job-post/{job-post-id}/walk-complete", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobPostId").value(responseDto.getJobPostId()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("수정된 게시글의 고유 번호")
                        )
                ));
    }

    @DisplayName("실시간 산책 중인 게시글 목록 조회 테스트")
    @Test
    public void getwalkinglist_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        JobPostWalkingResponseDto jobPostWalkingResponseDto1 = new JobPostWalkingResponseDto(1L, "알바생 이미지 주소", "알바생 닉네임", 1L,"강아지 이미지 주소","강아지 이름",1L);
        JobPostWalkingResponseDto jobPostWalkingResponseDto2 = new JobPostWalkingResponseDto(2L, "알바생 이미지 주소", "알바생 닉네임", 2L,"강아지 이미지 주소","강아지 이름",2L);

        Page<JobPostWalkingResponseDto> page = new PageImpl<>(Arrays.asList(jobPostWalkingResponseDto1, jobPostWalkingResponseDto2), PageRequest.of(0,10), 2);

        Mockito.when(jobPostService.getJobPostListOnWalking(Mockito.any(Pageable.class), Mockito.anyLong()))
                .thenReturn(page);

        //when & then
        mockMvc.perform(get("/api/job-post/walking")
                        .param("page","0")
                        .param("size","10")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("content[].partMemberId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 중인 알바생의 고유 번호"),
                                fieldWithPath("content[].partMemberProfileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 중인 알바생의 이미지 주소"),
                                fieldWithPath("content[].partMemberNickname")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 중인 알바생의 닉네임"),
                                fieldWithPath("content[].dogId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 중인 강아지의 고유 번호"),
                                fieldWithPath("content[].dogProfileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 중인 강아지의 이미지 주소"),
                                fieldWithPath("content[].dogName")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 중인 강아지의 이름"),
                                fieldWithPath("content[].jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 중인 게시글의 고유 번호"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                fieldWithPath("pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬된 상태"),
                                fieldWithPath("pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않은 상태"),
                                fieldWithPath("pageable.offset").type(JsonFieldType.NUMBER).description("오프셋"),
                                fieldWithPath("pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이지되지 않은 상태"),
                                fieldWithPath("pageable.paged").type(JsonFieldType.BOOLEAN).description("페이지 상태"),
                                fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("총 요소 수"),
                                fieldWithPath("last").type(JsonFieldType.BOOLEAN).description("마지막 페이지 여부"),
                                fieldWithPath("size").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("number").type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                fieldWithPath("sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
                                fieldWithPath("sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬된 상태"),
                                fieldWithPath("sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬되지 않은 상태"),
                                fieldWithPath("first").type(JsonFieldType.BOOLEAN).description("첫 페이지 여부"),
                                fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("현재 페이지의 요소 수"),
                                fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("페이지가 비어있는지 여부")
                        )
                ));

    }

}