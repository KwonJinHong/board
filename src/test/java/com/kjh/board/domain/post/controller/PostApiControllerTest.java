package com.kjh.board.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.condition.PostSearchCondition;
import com.kjh.board.domain.post.dto.PostInfoDto;
import com.kjh.board.domain.post.dto.PostPagingDto;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.UserService;
import com.kjh.board.global.jwt.service.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @PersistenceContext
    EntityManager em;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;
    final String USERNAME = "username1";

    private static User user;

    private void clear() {
        em.flush();
        em.clear();
    }

    @BeforeEach
    public void joinUser(){
        user = userRepository.save(User.builder().username(USERNAME).password("1q2w3e4r!!").nickname("졸린오후").email("dldldl@dldl.dl").phoneNumber("000-1111-2222").role(Role.USER).build());
        clear();
    }

    private String getAccessToken(){
        return jwtService.createAccessToken(USERNAME);
    }


    /**
     * 게시글 저장
     * */
    @Test
    public void 게시글_저장() throws Exception {
        //given
        Map<String, Object> map = new HashMap<>();
        map.put("title", "안뇽");
        map.put("content", "안뇽안뇽");
        String postSaveData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        post("/post")
                            .header("Authorization", "Bearer " + getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(postSaveData))
                .andExpect(status().isCreated());

        //then
        Assertions.assertThat(postRepository.findAll().size()).isEqualTo(1);
    }


    /**
     * 게시글 저장
     * */
    @Test
    public void 게시글_저장실패_제목이나_내용없음() throws Exception {
        //given
        Map<String, Object> map1 = new HashMap<>();
        map1.put("title", "안뇽");
        String postNoContentData = objectMapper.writeValueAsString(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("content", "안뇽");
        String postNoTitleData = objectMapper.writeValueAsString(map2);

        //when, then
        mockMvc.perform(
                        post("/post")
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postNoContentData))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        post("/post")
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postNoTitleData))
                .andExpect(status().isBadRequest());
    }

    /**
     * 게시글 조회
     * */
    @Test
    public void 게시글_조회() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(newUser);
        Post savePost = postRepository.save(post);

        //when
        MvcResult result = mockMvc.perform(
                get("/post/" + savePost.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
        ).andExpect(status().isOk()).andReturn();

        PostInfoDto postInfoDto = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), PostInfoDto.class);

        //then
        Assertions.assertThat(postInfoDto.getPostId()).isEqualTo(post.getId());
        Assertions.assertThat(postInfoDto.getContent()).isEqualTo(post.getContent());
        Assertions.assertThat(postInfoDto.getTitle()).isEqualTo(post.getTitle());
        Assertions.assertThat(postInfoDto.getUserDto().getNickname()).isEqualTo(newUser.getNickname());


    }
    
    /**
     * 게시글 수정
     * 제목만 수정
     * */
    @Test
    public void 게시글_수정_제목만() throws Exception {
        //given
        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();
        final String UPDATE_TITLE = "수정후제목";
        map.put("title", UPDATE_TITLE);
        String postUpdateTitleData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/post/" + savePost.getId())
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postUpdateTitleData))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITLE);
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo("수정전내용");
    }

    /**
     * 게시글 수정
     * 내용만 수정
     * */
    @Test
    public void 게시글_수정_내용만() throws Exception {
        //given
        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();

        final String UPDATE_CONTENT = "수정후내용";
        map.put("content", UPDATE_CONTENT);
        String postUpdateTitleData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/post/" + savePost.getId())
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postUpdateTitleData))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("수정전제목");
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
    }

    /**
     * 게시글 수정
     * 제목 + 내용 둘 다 수정
     * */
    @Test
    public void 게시글_수정_제목과내용() throws Exception {
        //given
        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();
        final String UPDATE_TITLE = "수정후제목";
        final String UPDATE_CONTENT = "수정후내용";
        map.put("title", UPDATE_TITLE);
        map.put("content", UPDATE_CONTENT);
        String postUpdateTitleData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/post/" + savePost.getId())
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postUpdateTitleData))
                .andExpect(status().isOk());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo(UPDATE_TITLE);
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
    }

    /**
     * 게시글 수정
     * 실패 -> 권한이 없음
     * */
    @Test
    public void 게시글_수정실패_권한없음() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(newUser);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();
        final String UPDATE_TITLE = "수정후제목";
        map.put("title", UPDATE_TITLE);
        String postUpdateTitleData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/post/" + savePost.getId())
                                .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postUpdateTitleData))
                .andExpect(status().isForbidden());


        //then
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo("수정전내용");
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("수정전제목");
    }

    /**
     * 게시글 삭제
     * */
    @Test
    public void 게시글_삭제() throws Exception {
        //given
        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        //when
        mockMvc.perform(
                delete("/post/" + savePost.getId())
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isOk());

        //then
        Assertions.assertThat(postRepository.findAll().size()).isEqualTo(0);
    }

    /**
     * 게시글 삭제
     * 실패 -> 권한이 없음
     * */
    @Test
    public void 게시글_삭제실패_권한없음() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        Post post = Post.builder().title("수정전제목").content("수정전내용").build();
        post.confirmWriter(newUser);
        Post savePost = postRepository.save(post);

        //when
        mockMvc.perform(
                        delete("/post/" + savePost.getId())
                                .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isForbidden());

        //then
        Assertions.assertThat(postRepository.findAll().size()).isEqualTo(1);
    }


    @Value("${spring.data.web.pageable.default-page-size}")
    private int pageCount;

    /**
     * 게시글 검색
     * 제목 + 내용으로 검색
     * 페이징 (생성된 시간순으로 정렬)
    * */
    @Test
    public void 게시글_제목과_내용으로_검색() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("제목"+ i).content("내용"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
            Thread.sleep(50); // 생성시간 텀을 주기 위해
        }

        Map<String, Object> map = new HashMap<>();
        map.put("title", "제목");
        map.put("content", "내용");


        String postSearchDto = objectMapper.writeValueAsString(map);
        clear();

        //when
        MvcResult result = mockMvc.perform(
                get("/post")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(postSearchDto)
        ).andExpect(status().isOk()).andReturn();


        //then
        PostPagingDto postList = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), PostPagingDto.class);

        Assertions.assertThat(postList.getTotalElementCount()).isEqualTo(POST_COUNT);
        Assertions.assertThat(postList.getCurrentPageElementCount()).isEqualTo(pageCount);
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(0).getTitle()).isEqualTo("제목50");
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(19).getTitle()).isEqualTo("제목31");

    }

    /**
     * 게시글 검색
     * 제목으로만 검색
     * 페이징 (생성된 시간순으로 정렬)
     * */
    @Test
    public void 게시글_제목으로만_검색() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("제목"+ i).content("내용"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
        }

        final int DIFF_POST_COUNT = 20;
        for(int i = 1; i<= DIFF_POST_COUNT; i++ ){
            Post post2 = Post.builder().title("AAA"+ i).content("BBB"+i).build();
            post2.confirmWriter(newUser);
            postRepository.save(post2);
            Thread.sleep(50); // 생성시간 텀을 두기 위해
        }

        Map<String, Object> map = new HashMap<>();
        map.put("title", "AAA");


        String postSearchDto = objectMapper.writeValueAsString(map);
        clear();

        //when
        MvcResult result = mockMvc.perform(
                get("/post")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postSearchDto)
        ).andExpect(status().isOk()).andReturn();


        //then
        PostPagingDto postList = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), PostPagingDto.class);

        Assertions.assertThat(postList.getTotalElementCount()).isEqualTo(DIFF_POST_COUNT);
        Assertions.assertThat(postList.getCurrentPageElementCount()).isEqualTo(pageCount);
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(0).getTitle()).isEqualTo("AAA20");
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(19).getTitle()).isEqualTo("AAA1");

    }



    /**
     * 게시글 검색
     * 내용으로만 검색
     * 페이징 (생성된 시간순으로 정렬)
     * */
    @Test
    public void 게시글_내용으로만_검색() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("제목"+ i).content("내용"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
        }

        final int DIFF_POST_COUNT = 20;
        for(int i = 1; i<= DIFF_POST_COUNT; i++ ){
            Post post2 = Post.builder().title("AAA"+ i).content("BBB"+i).build();
            post2.confirmWriter(newUser);
            postRepository.save(post2);
            Thread.sleep(50); // 생성시간 텀을 두기 위해
        }

        Map<String, Object> map = new HashMap<>();
        map.put("content", "BBB");


        String postSearchDto = objectMapper.writeValueAsString(map);
        clear();

        //when
        MvcResult result = mockMvc.perform(
                get("/post")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postSearchDto)
        ).andExpect(status().isOk()).andReturn();


        //then
        PostPagingDto postList = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), PostPagingDto.class);

        Assertions.assertThat(postList.getTotalElementCount()).isEqualTo(DIFF_POST_COUNT);
        Assertions.assertThat(postList.getCurrentPageElementCount()).isEqualTo(pageCount);
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(0).getContent()).isEqualTo("BBB20");
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(19).getContent()).isEqualTo("BBB1");

    }

    /**
     * 게시글 검색 실패
     * 검색 조건이 없음
     * */
    @Test
    public void 게시글_검색실패_검색조건없음() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("새삥").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("제목"+ i).content("내용"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
        }


        clear();

        //when, then
        MvcResult result = mockMvc.perform(
                get("/post")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Authorization", "Bearer " + getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();

    }
}