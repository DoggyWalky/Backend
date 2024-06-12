package com.doggyWalky.doggyWalky.apply.controller;

import com.doggyWalky.doggyWalky.apply.dto.request.NewApplyRequestDto;
import com.doggyWalky.doggyWalky.apply.entity.Apply;
import com.doggyWalky.doggyWalky.apply.repository.ApplyRepository;
import com.doggyWalky.doggyWalky.common.RestDocsTestSupport;
import com.doggyWalky.doggyWalky.constant.ConstantPool;
import com.doggyWalky.doggyWalky.dog.dto.RegisterDogRequest;
import com.doggyWalky.doggyWalky.dog.entity.Dog;
import com.doggyWalky.doggyWalky.dog.repository.DogRepository;
import com.doggyWalky.doggyWalky.jobpost.dto.JobPostRegisterRequest;
import com.doggyWalky.doggyWalky.jobpost.entity.JobPost;
import com.doggyWalky.doggyWalky.jobpost.entity.Status;
import com.doggyWalky.doggyWalky.jobpost.repository.JobPostRepository;
import com.doggyWalky.doggyWalky.member.entity.Member;
import com.doggyWalky.doggyWalky.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureRestDocs
@Transactional
@DirtiesContext
class ApplyControllerTest extends RestDocsTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private ApplyRepository applyRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    public JobPost jobPost;
    public JobPost jobPost2;


    private String emailKangwook = "BLv8Uug7klqfirsmyHa/q1mzxA8nma90rdYVZdc60fY=";

    private String emailTaewon = "rZMKY8bymVHluFwIYbtX6Qeve1OrNAFg+AJ8AzijpdQ=";

    @BeforeEach
    public void setUp() {
        UserDetails taewonUserDetails = userDetailsService.loadUserByUsername(emailTaewon);
        Member taewon = memberRepository.findById(Long.parseLong(taewonUserDetails.getUsername())).get();

        UserDetails kangwookUserDetails = userDetailsService.loadUserByUsername(emailKangwook);
        Member kangwook = memberRepository.findById(Long.parseLong(kangwookUserDetails.getUsername())).get();

        // 강아지 생성
        RegisterDogRequest request = new RegisterDogRequest("3","말티즈","보리","소형견");
        Dog dog = request.toEntity(taewon);
        dogRepository.save(dog);

        // 게시글 생성
        JobPostRegisterRequest postRequest = new JobPostRegisterRequest("게시글 1번","말티즈 산책 부탁드리겠습니다.", Status.WAITING,"출발지","목적지",dog.getDogId());
        JobPost jobPost = postRequest.toEntity();
        jobPost.assignWriter(taewon);
        jobPost.setDefaultImage("https://doggywalky-bucket.s3.ap-northeast-2.amazonaws.com/basic_board_image.jpg");
        jobPostRepository.save(jobPost);
        this.jobPost = jobPost;

        // 강아지 생성
        RegisterDogRequest request2 = new RegisterDogRequest("10","진돗개","진도","중형견");
        Dog dog2 = request2.toEntity(taewon);
        dogRepository.save(dog2);

        // 게시글 생성
        JobPostRegisterRequest postRequest2 = new JobPostRegisterRequest("게시글 2번","진돗개 산책 부탁드리겠습니다.", Status.WAITING,"출발지","목적지",dog2.getDogId());
        JobPost jobPost2 = postRequest2.toEntity();
        jobPost2.assignWriter(taewon);
        jobPost2.setDefaultImage("https://doggywalky-bucket.s3.ap-northeast-2.amazonaws.com/basic_board_image.jpg");
        jobPostRepository.save(jobPost2);
        this.jobPost2 = jobPost2;


    }


    @DisplayName("신청 등록하기 테스트")
    @Test
    public void apply_200() throws Exception {
        // given
        UserDetails taewonUserDetails = userDetailsService.loadUserByUsername(emailTaewon);
        Member taewon = memberRepository.findById(Long.parseLong(taewonUserDetails.getUsername())).get();

        UserDetails kangwookUserDetails = userDetailsService.loadUserByUsername(emailKangwook);
        Member kangwook = memberRepository.findById(Long.parseLong(kangwookUserDetails.getUsername())).get();

        NewApplyRequestDto dto = new NewApplyRequestDto(jobPost.getId(),taewon.getId());

        // 요청 메시지 바디에 JSON 형태로 넣어주기 위해 객체 직렬화 합니다.
        String applyRequestDto = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/api/apply")
                        .with(SecurityMockMvcRequestPostProcessors.user(kangwookUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applyRequestDto))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.jobPostId").value(jobPost.getId()))
                .andExpect(jsonPath("$.ownerId").value(taewon.getId()))
                .andExpect(jsonPath("$.workerId").value(kangwook.getId()))
                .andExpect(jsonPath("$.status").value(ConstantPool.ApplyStatus.WAIT.name()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        requestFields(  // 요청 필드 추가
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책을 신청할 게시글 고유 번호")
                                        .attributes(constraints("산책 신청할 게시글의 PK가 들어와야합니다.")),
                                fieldWithPath("ownerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책할 강아지의 주인 고유 번호")
                                        .attributes(constraints("산책시킬 강아지 주인의 PK가 들어와야합니다."))
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("applyId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("생성된 신청의 고유 번호"),
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책을 신청한 게시글의 고유 번호"),
                                fieldWithPath("ownerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책시킬 강아지의 주인 고유 번호"),
                                fieldWithPath("workerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 해주는 알바생의 고유 번호"),
                                fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청의 상태")
                                        .attributes(constraints("산책 신청의 상태는  ACCEPT, REFUSE, WAIT가 있습니다.")),
                                fieldWithPath("createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 생성 일시"),
                                fieldWithPath("updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 수정 일시")
                        )
                ));
    }

    @DisplayName("신청 목록 조회하기 테스트")
    @Test
    public void getapplylist_200() throws Exception {

        // given
        UserDetails taewonUserDetails = userDetailsService.loadUserByUsername(emailTaewon);
        Member taewon = memberRepository.findById(Long.parseLong(taewonUserDetails.getUsername())).get();

        UserDetails kangwookUserDetails = userDetailsService.loadUserByUsername(emailKangwook);
        Member kangwook = memberRepository.findById(Long.parseLong(kangwookUserDetails.getUsername())).get();

        // 신청 생성
        Apply apply = Apply.builder().jobPost(jobPost).owner(taewon).worker(kangwook).status(ConstantPool.ApplyStatus.WAIT).build();
        applyRepository.save(apply);


        // when
        mockMvc.perform(get("/api/apply/job-post/{job-post-id}",jobPost.getId())
                        .param("page","0")
                        .param("size","10")
                        .param("sort","createdDate,DESC")
                        .with(SecurityMockMvcRequestPostProcessors.user(taewonUserDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("content[].applyId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("생성된 신청의 고유 번호"),
                                fieldWithPath("content[].jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책을 신청한 게시글의 고유 번호"),
                                fieldWithPath("content[].ownerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책시킬 강아지의 주인 고유 번호"),
                                fieldWithPath("content[].workerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 해주는 알바생의 고유 번호"),
                                fieldWithPath("content[].workerName")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 해주는 알바생의 이름(실명)"),
                                fieldWithPath("content[].workerNickName")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 해주는 알바생의 닉네임"),
                                fieldWithPath("content[].workerDescription")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 해주는 알바생의 자기소개글"),
                                fieldWithPath("content[].workerProfileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 해주는 알바생의 프로필 이미지 주소"),
                                fieldWithPath("content[].status")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청의 수락 상태")
                                        .attributes(constraints("산책 신청의 수락 상태는  ACCEPT(승인), REFUSE(거절), WAIT(대기)가 있습니다.")),
                                fieldWithPath("content[].createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 생성 일시"),
                                fieldWithPath("content[].updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 수정 일시"),
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

    @DisplayName("신청 수락하기 테스트")
    @Test
    public void acceptapply_200() throws Exception {
        // given
        UserDetails taewonUserDetails = userDetailsService.loadUserByUsername(emailTaewon);
        Member taewon = memberRepository.findById(Long.parseLong(taewonUserDetails.getUsername())).get();

        UserDetails kangwookUserDetails = userDetailsService.loadUserByUsername(emailKangwook);
        Member kangwook = memberRepository.findById(Long.parseLong(kangwookUserDetails.getUsername())).get();

        // 신청 생성
        Apply apply = Apply.builder().jobPost(jobPost).owner(taewon).worker(kangwook).status(ConstantPool.ApplyStatus.WAIT).build();
        applyRepository.save(apply);

        // when
        mockMvc.perform(post("/api/apply/{apply-id}/accept",apply.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(taewonUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applyId").value(apply.getId()))
                .andExpect(jsonPath("$.jobPostId").value(jobPost.getId()))
                .andExpect(jsonPath("$.ownerId").value(taewon.getId()))
                .andExpect(jsonPath("$.workerId").value(kangwook.getId()))
                .andExpect(jsonPath("$.status").value(ConstantPool.ApplyStatus.ACCEPT.name()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("applyId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("생성된 신청의 고유 번호"),
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책을 신청한 게시글의 고유 번호"),
                                fieldWithPath("ownerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책시킬 강아지의 주인 고유 번호"),
                                fieldWithPath("workerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 해주는 알바생의 고유 번호"),
                                fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청의 상태")
                                        .attributes(constraints("산책 신청의 상태는  ACCEPT(승인), REFUSE(거절), WAIT(대기)가 있습니다. 여기서는 ACCEPT여야 수락된 것입니다.")),
                                fieldWithPath("createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 생성 일시"),
                                fieldWithPath("updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 수정 일시")
                        )
                ));

    }

    @DisplayName("신청 거절하기 테스트")
    @Test
    public void refuseapply_200() throws Exception {
        // given
        UserDetails taewonUserDetails = userDetailsService.loadUserByUsername(emailTaewon);
        Member taewon = memberRepository.findById(Long.parseLong(taewonUserDetails.getUsername())).get();

        UserDetails kangwookUserDetails = userDetailsService.loadUserByUsername(emailKangwook);
        Member kangwook = memberRepository.findById(Long.parseLong(kangwookUserDetails.getUsername())).get();

        // 신청 생성
        Apply apply = Apply.builder().jobPost(jobPost).owner(taewon).worker(kangwook).status(ConstantPool.ApplyStatus.WAIT).build();
        applyRepository.save(apply);

        // when
        mockMvc.perform(post("/api/apply/{apply-id}/refuse",apply.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(taewonUserDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applyId").value(apply.getId()))
                .andExpect(jsonPath("$.jobPostId").value(jobPost.getId()))
                .andExpect(jsonPath("$.ownerId").value(taewon.getId()))
                .andExpect(jsonPath("$.workerId").value(kangwook.getId()))
                .andExpect(jsonPath("$.status").value(ConstantPool.ApplyStatus.REFUSE.name()))
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("applyId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("생성된 신청의 고유 번호"),
                                fieldWithPath("jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책을 신청한 게시글의 고유 번호"),
                                fieldWithPath("ownerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책시킬 강아지의 주인 고유 번호"),
                                fieldWithPath("workerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 해주는 알바생의 고유 번호"),
                                fieldWithPath("status")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청의 상태")
                                        .attributes(constraints("산책 신청의 상태는  ACCEPT(승인), REFUSE(거절), WAIT(대기)가 있습니다. 여기서는 REFUSE여야 거절된 것입니다.")),
                                fieldWithPath("createdAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 생성 일시"),
                                fieldWithPath("updatedAt")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청 수정 일시")
                        )
                ));


    }

    @DisplayName("내가 신청한 목록 조회 테스트")
    @Test
    public void getmyapplylist_200() throws Exception  {

        // given
        UserDetails taewonUserDetails = userDetailsService.loadUserByUsername(emailTaewon);
        Member taewon = memberRepository.findById(Long.parseLong(taewonUserDetails.getUsername())).get();

        UserDetails kangwookUserDetails = userDetailsService.loadUserByUsername(emailKangwook);
        Member kangwook = memberRepository.findById(Long.parseLong(kangwookUserDetails.getUsername())).get();

        // 신청 생성
        Apply apply1 = Apply.builder().jobPost(jobPost).owner(taewon).worker(kangwook).status(ConstantPool.ApplyStatus.WAIT).build();
        applyRepository.save(apply1);

        Apply apply2 = Apply.builder().jobPost(jobPost2).owner(taewon).worker(kangwook).status(ConstantPool.ApplyStatus.WAIT).build();
        applyRepository.save(apply2);

        // when
        mockMvc.perform(get("/api/apply/my-apply")
                        .param("page","0")
                        .param("size","10")
                        .param("sort","createdDate,DESC")
                        .with(SecurityMockMvcRequestPostProcessors.user(kangwookUserDetails)))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        requestHeaders( // 요청 헤더 추가
                                headerWithName("Authorization")
                                        .description("Bearer 토큰")
                        ),
                        responseFields( // 응답 필드 추가
                                fieldWithPath("content[].writerId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 신청한 게시글 작성자의 고유 번호"),
                                fieldWithPath("content[].nickName")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청한 게시글 작성자의 닉네임"),
                                fieldWithPath("content[].profileImage")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청한 게시글 작성자의 프로필 이미지 주소"),
                                fieldWithPath("content[].jobPostId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 신청한 게시글의 고유 번호"),
                                fieldWithPath("content[].title")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청한 게시글의 제목"),
                                fieldWithPath("content[].postImage")
                                        .type(JsonFieldType.STRING)
                                        .optional().description("산책 신청한 게시글의 이미지 주소"),
                                fieldWithPath("content[].status")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청한 게시글의 구인 상태")
                                        .attributes(constraints("게시글의 구인 상태는 WAITING(대기), RESERVED(예약)가 있습니다.")),
                                fieldWithPath("content[].dogId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책시킬 강아지의 고유 번호"),
                                fieldWithPath("content[].kind")
                                        .type(JsonFieldType.STRING)
                                        .description("산책시킬 강아지의 분류"),
                                fieldWithPath("content[].applyId")
                                        .type(JsonFieldType.NUMBER)
                                        .description("산책 신청의 고유 번호"),
                                fieldWithPath("content[].applyStatus")
                                        .type(JsonFieldType.STRING)
                                        .description("산책 신청의 수락 상태")
                                        .attributes(constraints("산책 신청의 수락 상태는  ACCEPT(승인), REFUSE(거절), WAIT(대기)가 있습니다.")),
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