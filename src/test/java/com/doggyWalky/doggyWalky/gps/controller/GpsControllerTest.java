package com.doggyWalky.doggyWalky.gps.controller;

import com.doggyWalky.doggyWalky.apply.entity.Apply;
import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.gps.dto.request.GpsRequestDto;
import com.doggyWalky.doggyWalky.gps.dto.response.GpsResponseDto;
import com.doggyWalky.doggyWalky.gps.service.GpsService;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.report.dto.response.ReportResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class GpsControllerTest extends RestDocsTestSupport  {

    @MockBean
    private GpsService gpsService;


    @MockBean
    private JobLauncher jobLauncher;


    @Autowired
    private UserDetailsService userDetailsService;

    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("gps 등록 테스트")
    @Test
    public void savegps_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        GpsRequestDto gpsDto1 = new GpsRequestDto(1L, 10d, 30d, 30L);
        GpsRequestDto gpsDto2 = new GpsRequestDto(1L, 20d, 30d, 30L);
        GpsRequestDto gpsDto3 = new GpsRequestDto(1L, 30d, 30d, 30L);
        GpsRequestDto gpsDto4 = new GpsRequestDto(1L, 40d, 30d, 30L);
        GpsRequestDto gpsDto5 = new GpsRequestDto(1L, 50d, 30d, 30L);

        List<GpsRequestDto> gpsList = Arrays.asList(gpsDto1,gpsDto2,gpsDto3,gpsDto4,gpsDto5);
        String gpsListToJson = objectMapper.writeValueAsString(gpsList);

        Mockito.doNothing().when(gpsService).saveGpsList(gpsList);

        //when & then
        mockMvc.perform(post("/api/gps")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gpsListToJson))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("[].jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 게시글 고유 번호"),
                                fieldWithPath("[].latitude")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 위도"),
                                fieldWithPath("[].longitude")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 경도"),
                                fieldWithPath("[].timestamp")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 타임스탬프")
                        )
                ));
    }

    @DisplayName("스프링 배치를 이용한 gps 등록 테스트")
    @Test
    public void savegpsbybatch_200() throws Exception {
        //given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        GpsRequestDto gpsDto1 = new GpsRequestDto(1L, 10d, 30d, 30L);
        GpsRequestDto gpsDto2 = new GpsRequestDto(1L, 20d, 30d, 30L);
        GpsRequestDto gpsDto3 = new GpsRequestDto(1L, 30d, 30d, 30L);
        GpsRequestDto gpsDto4 = new GpsRequestDto(1L, 40d, 30d, 30L);
        GpsRequestDto gpsDto5 = new GpsRequestDto(1L, 50d, 30d, 30L);

        List<GpsRequestDto> gpsList = Arrays.asList(gpsDto1,gpsDto2,gpsDto3,gpsDto4,gpsDto5);
        String gpsListToJson = objectMapper.writeValueAsString(gpsList);

        // JobExecution 객체를 모킹합니다.
        JobExecution jobExecution = Mockito.mock(JobExecution.class);
        Mockito.when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(jobExecution);

        //when & then
        mockMvc.perform(post("/api/gps-by-batch/{post-id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gpsListToJson))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("[].jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 게시글 고유 번호"),
                                fieldWithPath("[].latitude")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 위도"),
                                fieldWithPath("[].longitude")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 경도"),
                                fieldWithPath("[].timestamp")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 경로 관련 타임스탬프")
                        )
                ));
    }

    @DisplayName("게시글 관련 산책 경로 목록 조회")
    @Test
    public void getgpslist_200() throws Exception {

        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        GpsResponseDto responseDto1 = new GpsResponseDto(1L,30D,30D,LocalDateTime.now());
        GpsResponseDto responseDto2 = new GpsResponseDto(2L,30D,30D,LocalDateTime.now());
        GpsResponseDto responseDto3 = new GpsResponseDto(3L,30D,30D,LocalDateTime.now());
        GpsResponseDto responseDto4 = new GpsResponseDto(4L,30D,30D,LocalDateTime.now());


        Page<GpsResponseDto> pages = new PageImpl<>(Arrays.asList(responseDto1, responseDto2, responseDto3, responseDto4), PageRequest.of(0, 10), 4);
        Mockito.when(gpsService.getGpsList(anyLong(), any(Pageable.class))).thenReturn(pages);

        // when
        mockMvc.perform(get("/api/gps/job-post/{job-post-id}",1L)
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
                                fieldWithPath("content[].gpsId").type(JsonFieldType.NUMBER).description("산책 경로 관련 게시글 고유 번호"),
                                fieldWithPath("content[].latitude").type(JsonFieldType.NUMBER).description("산책 경로 관련 위도)"),
                                fieldWithPath("content[].longitude").type(JsonFieldType.NUMBER).description("산책 경로 관련 경도"),
                                fieldWithPath("content[].coordinateTime").type(JsonFieldType.STRING).description("신산책 경로 관련 타임스탬프"),
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



}


