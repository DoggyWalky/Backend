package com.doggyWalky.doggyWalky.file.controller;

import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.file.common.TableName;
import com.doggyWalky.doggyWalky.file.dto.request.FileRequestDto;
import com.doggyWalky.doggyWalky.file.repository.FileRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class FileControllerTest extends RestDocsTestSupport {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    private String email = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    @DisplayName("파일 추가하기")
    @Test
    public void postfile_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        MockMultipartFile file1 = new MockMultipartFile("files", "file1.txt", "text/plain", "file content".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "file2.txt", "text/plain", "file content".getBytes());

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/files")
                        .file(file1)
                        .file(file2)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value(startsWith("file1.txt")))
                .andExpect(jsonPath("$[1].fileName").value(startsWith("file2.txt")))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestParts(partWithName("files").description("업로드할 파일 목록(form-data 형태의 파일 목록)")) // 요청에 파일 목록 추가
                        ,
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].fileId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("추가된 파일의 고유 번호"),
                                fieldWithPath("[].size")
                                        .type(JsonFieldType.NUMBER)
                                        .description("추가된 파일의 크기"),
                                fieldWithPath("[].path")
                                        .type(JsonFieldType.STRING)
                                        .description("추가된 파일의 S3 주소"),
                                fieldWithPath("[].fileName")
                                        .type(JsonFieldType.STRING)
                                        .description("추가된 파일의 이름(원래 파일 이름에 UUID 추가된 형태)"),
                                fieldWithPath("[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("추가된 파일의 생성 일자"),
                                fieldWithPath("[].fileInfoId")
                                        .type(JsonFieldType.NULL)
                                        .description("추가된 파일 정보의 고유 번호(해당 API에서는 NULL값)")
                        )
                ));

    }

    @DisplayName("파일 목록 조회하기")
    @Test
    public void getfilelist_200() throws Exception {
        // given
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        FileRequestDto dto = FileRequestDto.create(TableName.MEMBER_PROFILE_INFO,5L);

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String fileRequestDto = objectMapper.writeValueAsString(dto);


        // when & then
        mockMvc.perform(get("/api/files")
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                        .contentType(APPLICATION_JSON)
                        .content(fileRequestDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("눈치.png"))
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("tableName")
                                        .type(JsonFieldType.STRING)
                                        .description("테이블명")
                                        .attributes(constraints("테이블명은 파일과 관련된 테이블명으로 MEMBER_PROFILE_INFO, DOG, JOB_POST가 들어와야합니다.")),
                                fieldWithPath("tableKey")
                                        .type(JsonFieldType.NUMBER)
                                        .description("테이블키")
                                        .attributes(constraints("테이블키는 파일과 관련된 테이블의 PK를 의미합니다."))
                        )
                        ,
                        responseFields( // 응답 필드 추가
                                fieldWithPath("[].fileId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("추가된 파일의 고유 번호"),
                                fieldWithPath("[].size")
                                        .type(JsonFieldType.NUMBER)
                                        .description("추가된 파일의 크기"),
                                fieldWithPath("[].path")
                                        .type(JsonFieldType.STRING)
                                        .description("추가된 파일의 S3 주소"),
                                fieldWithPath("[].fileName")
                                        .type(JsonFieldType.STRING)
                                        .description("추가된 파일의 이름"),
                                fieldWithPath("[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("추가된 파일의 생성 일자"),
                                fieldWithPath("[].fileInfoId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("추가된 파일 정보의 고유 번호")
                        )
                ));
    }

}