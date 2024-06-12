package com.doggyWalky.doggyWalky.report.controller;

import com.doggyWalky.doggyWalky.chat.dto.response.ChatMessageResponse;
import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.report.dto.condition.ReportSearchCondition;
import com.doggyWalky.doggyWalky.report.dto.request.ChatReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.request.ReportRequestDto;
import com.doggyWalky.doggyWalky.report.dto.response.ReportResponseDto;
import com.doggyWalky.doggyWalky.report.dto.response.SimpleReportResponseDto;
import com.doggyWalky.doggyWalky.report.service.ReportService;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class ReportControllerTest extends RestDocsTestSupport {

    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private ReportService reportService;

    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("신고 등록하기")
    @Test
    public void report_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        ReportRequestDto reportRequestDto = new ReportRequestDto(1L, 3L, "채팅방에서 욕설을 하였습니다. 기분이 너무 나빠서 신고합니다.",ReportRequestDto.Type.ABUSE);

        SimpleReportResponseDto simpleReportResponseDto = new SimpleReportResponseDto(1L);

        Mockito.when(reportService.registerReport(anyLong(), any(ReportRequestDto.class))).thenReturn(simpleReportResponseDto);

        //when & then
        mockMvc.perform(post("/api/reports")
                    .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportId").value(1L))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("targetId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("신고할 대상의 고유 번호"),
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("신고가 발생한 게시글의 고유 번호"),
                                fieldWithPath("reportContent")
                                        .type(JsonFieldType.STRING)
                                        .description("신고 내용")
                                        .attributes(constraints("신고 내용은 최소 20글자 이상 최대 500글자 이하를 작성하셔야 합니다.")),
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("신고 분류")
                                        .attributes(constraints("신고 분류는 ABUSE(욕설), NONFULFILMENT(계약 불이행), PUPPYLOST(강아지 분실)가 있습니다."))
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("reportId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("생성된 신고의 고유 번호")
                        )
                ));

    }

    @DisplayName("신고 목록 조회하기")
    @Test
    public void getreportlist_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        ReportSearchCondition condition = new ReportSearchCondition(1L, 1L, ReportRequestDto.Type.NONFULFILMENT, "불이행", LocalDateTime.now(), LocalDateTime.now());

        ReportResponseDto report1 = new ReportResponseDto(
                1L, "Reporter 1", "Nick 1", "Description 1", "profile1.jpg", "010-1234-5678",
                2L, "Target 1", "Target Nick 1", "Target Description 1", "targetProfile1.jpg", "010-8765-4321",
                3L, ReportRequestDto.Type.NONFULFILMENT, "Report Content 1", LocalDateTime.now());

        ReportResponseDto report2 = new ReportResponseDto(
                4L, "Reporter 2", "Nick 2", "Description 2", "profile2.jpg", "010-2345-6789",
                5L, "Target 2", "Target Nick 2", "Target Description 2", "targetProfile2.jpg", "010-9876-5432",
                6L, ReportRequestDto.Type.NONFULFILMENT, "Report Content 2", LocalDateTime.now());

        ReportResponseDto report3 = new ReportResponseDto(
                7L, "Reporter 3", "Nick 3", "Description 3", "profile3.jpg", "010-3456-7890",
                8L, "Target 3", "Target Nick 3", "Target Description 3", "targetProfile3.jpg", "010-8765-4321",
                9L, ReportRequestDto.Type.NONFULFILMENT, "Report Content 3", LocalDateTime.now());

        Page<ReportResponseDto> pages = new PageImpl<>(Arrays.asList(report1, report2, report3), PageRequest.of(0, 10), 3);

        Mockito.when(reportService.getReportList(any(ReportSearchCondition.class), any(Pageable.class)))
                .thenReturn(pages);

        //when & then
        mockMvc.perform(get("/api/reports")
                        .param("page","0")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(condition))
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("reporterId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("검색하려는 신고자의 고유 번호"),
                                fieldWithPath("targetId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("검색하려는 신고 대상자의 고유 번호"),
                                fieldWithPath("type")
                                        .type(JsonFieldType.STRING)
                                        .description("검색하려는 신고 분류")
                                        .attributes(constraints("신고 분류는 ABUSE(욕설), NONFULFILMENT(계약 불이행), PUPPYLOST(강아지 분실)가 있습니다.")),
                                fieldWithPath("reportContent")
                                        .type(JsonFieldType.STRING)
                                        .description("검색하려는 신고 내용"),
                                fieldWithPath("startDate")
                                        .type(JsonFieldType.VARIES)
                                        .description("검색하려는 시작 일시"),
                                fieldWithPath("endDate")
                                        .type(JsonFieldType.VARIES)
                                        .description("검색하려는 종료 일시")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("content[].reporterId").type(JsonFieldType.NUMBER).description("신고자의 고유 번호"),
                                fieldWithPath("content[].reporterName").type(JsonFieldType.STRING).description("신고자의 이름(실명)"),
                                fieldWithPath("content[].reporterNickName").type(JsonFieldType.STRING).description("신고자의 닉네임"),
                                fieldWithPath("content[].reporterDescription").type(JsonFieldType.STRING).description("신고자의 자기소개글"),
                                fieldWithPath("content[].reporterProfileImage").type(JsonFieldType.STRING).description("신고자의 프로필 이미지 주소"),
                                fieldWithPath("content[].reporterPhoneNumber").type(JsonFieldType.STRING).description("신고자의 전화번호"),
                                fieldWithPath("content[].targetId").type(JsonFieldType.NUMBER).description("신고 대상자의 고유 번호"),
                                fieldWithPath("content[].targetName").type(JsonFieldType.STRING).description("신고 대상자의 이름(실명)"),
                                fieldWithPath("content[].targetNickName").type(JsonFieldType.STRING).description("신고 대상자의 닉네임"),
                                fieldWithPath("content[].targetDescription").type(JsonFieldType.STRING).description("신고 대상자의 자기소개글"),
                                fieldWithPath("content[].targetProfileImage").type(JsonFieldType.STRING).description("신고 대상자의 프로필 이미지 주소"),
                                fieldWithPath("content[].targetPhoneNumber").type(JsonFieldType.STRING).description("신고 대상자의 전화번호"),
                                fieldWithPath("content[].jobPostId").type(JsonFieldType.NUMBER).description("신고가 발생한 게시물의 고유 번호"),
                                fieldWithPath("content[].type").type(JsonFieldType.STRING).description("신고 분류").attributes(constraints("신고 분류는 ABUSE(욕설), NONFULFILMENT(계약 불이행), PUPPYLOST(강아지 분실)가 있습니다.")),
                                fieldWithPath("content[].reportContent").type(JsonFieldType.STRING).description("신고 내용"),
                                fieldWithPath("content[].createdAt").type(JsonFieldType.STRING).description("신고 생성 일시"),
                                fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("페이지 번호"),
                                fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("페이지 크기"),
                                fieldWithPath("pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("정렬 정보가 비어 있는지 여부"),
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

    @DisplayName("채팅 욕설 신고에 대한 채팅 리스트 조회하기")
    @Test
    public void getchatlistforreport_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        ChatReportRequestDto chatReportRequestDto = new ChatReportRequestDto(1L,1L, 3L);

        ChatMessageResponse chat1 = new ChatMessageResponse(1L,1L,LocalDateTime.now(),"욕설 채팅",true,false);
        ChatMessageResponse chat2 = new ChatMessageResponse(2L,3L,LocalDateTime.now(),"욕하지 마세요.",true,false);
        ChatMessageResponse chat3 = new ChatMessageResponse(3L,1L,LocalDateTime.now(),"욕설 채팅",true,false);

        List<ChatMessageResponse> chatLists = Arrays.asList(chat1, chat2, chat3);

        Mockito.when(reportService.getChatListForReports(any(ChatReportRequestDto.class), anyLong()))
                .thenReturn(chatLists);

        //when & then
        mockMvc.perform(get("/api/reports/{report-id}/chat-list",3L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReportRequestDto))
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("targetId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("신고 대상자의 고유 번호"),
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("신고가 발생한 게시글의 고유 번호"),
                                fieldWithPath("reporterId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("신고자의 고유 번호")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].chatId").type(JsonFieldType.NUMBER).description("채팅의 고유 번호"),
                                fieldWithPath("[].memberId").type(JsonFieldType.NUMBER).description("채팅을 작성한 사용자의 고유 번호"),
                                fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("채팅 생성 일시"),
                                fieldWithPath("[].content").type(JsonFieldType.STRING).description("채팅 내용"),
                                fieldWithPath("[].readYn").type(JsonFieldType.BOOLEAN).description("채팅 읽음 유무"),
                                fieldWithPath("[].deleteYn").type(JsonFieldType.BOOLEAN).description("채팅 삭제 여부")
                        )
                ));

    }



}