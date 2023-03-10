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
        user = userRepository.save(User.builder().username(USERNAME).password("1q2w3e4r!!").nickname("????????????").email("dldldl@dldl.dl").phoneNumber("000-1111-2222").role(Role.USER).build());
        clear();
    }

    private String getAccessToken(){
        return jwtService.createAccessToken(USERNAME);
    }


    /**
     * ????????? ??????
     * */
    @Test
    public void ?????????_??????() throws Exception {
        //given
        Map<String, Object> map = new HashMap<>();
        map.put("title", "??????");
        map.put("content", "????????????");
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
     * ????????? ??????
     * */
    @Test
    public void ?????????_????????????_????????????_????????????() throws Exception {
        //given
        Map<String, Object> map1 = new HashMap<>();
        map1.put("title", "??????");
        String postNoContentData = objectMapper.writeValueAsString(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("content", "??????");
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
     * ????????? ??????
     * */
    @Test
    public void ?????????_??????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        Post post = Post.builder().title("???????????????").content("???????????????").build();
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
     * ????????? ??????
     * ????????? ??????
     * */
    @Test
    public void ?????????_??????_?????????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();
        final String UPDATE_TITLE = "???????????????";
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
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo("???????????????");
    }

    /**
     * ????????? ??????
     * ????????? ??????
     * */
    @Test
    public void ?????????_??????_?????????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();

        final String UPDATE_CONTENT = "???????????????";
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
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("???????????????");
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo(UPDATE_CONTENT);
    }

    /**
     * ????????? ??????
     * ?????? + ?????? ??? ??? ??????
     * */
    @Test
    public void ?????????_??????_???????????????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(user);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();
        final String UPDATE_TITLE = "???????????????";
        final String UPDATE_CONTENT = "???????????????";
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
     * ????????? ??????
     * ?????? -> ????????? ??????
     * */
    @Test
    public void ?????????_????????????_????????????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        Post post = Post.builder().title("???????????????").content("???????????????").build();
        post.confirmWriter(newUser);
        Post savePost = postRepository.save(post);

        Map<String, Object> map = new HashMap<>();
        final String UPDATE_TITLE = "???????????????";
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
        Assertions.assertThat(postRepository.findAll().get(0).getContent()).isEqualTo("???????????????");
        Assertions.assertThat(postRepository.findAll().get(0).getTitle()).isEqualTo("???????????????");
    }

    /**
     * ????????? ??????
     * */
    @Test
    public void ?????????_??????() throws Exception {
        //given
        Post post = Post.builder().title("???????????????").content("???????????????").build();
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
     * ????????? ??????
     * ?????? -> ????????? ??????
     * */
    @Test
    public void ?????????_????????????_????????????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        Post post = Post.builder().title("???????????????").content("???????????????").build();
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
     * ????????? ??????
     * ?????? + ???????????? ??????
     * ????????? (????????? ??????????????? ??????)
    * */
    @Test
    public void ?????????_?????????_????????????_??????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("??????"+ i).content("??????"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
            Thread.sleep(50); // ???????????? ?????? ?????? ??????
        }

        Map<String, Object> map = new HashMap<>();
        map.put("title", "??????");
        map.put("content", "??????");


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
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(0).getTitle()).isEqualTo("??????50");
        Assertions.assertThat(postList.getSimpleLectureDtoList().get(19).getTitle()).isEqualTo("??????31");

    }

    /**
     * ????????? ??????
     * ??????????????? ??????
     * ????????? (????????? ??????????????? ??????)
     * */
    @Test
    public void ?????????_???????????????_??????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("??????"+ i).content("??????"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
        }

        final int DIFF_POST_COUNT = 20;
        for(int i = 1; i<= DIFF_POST_COUNT; i++ ){
            Post post2 = Post.builder().title("AAA"+ i).content("BBB"+i).build();
            post2.confirmWriter(newUser);
            postRepository.save(post2);
            Thread.sleep(50); // ???????????? ?????? ?????? ??????
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
     * ????????? ??????
     * ??????????????? ??????
     * ????????? (????????? ??????????????? ??????)
     * */
    @Test
    public void ?????????_???????????????_??????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("??????"+ i).content("??????"+i).build();
            post.confirmWriter(newUser);
            postRepository.save(post);
        }

        final int DIFF_POST_COUNT = 20;
        for(int i = 1; i<= DIFF_POST_COUNT; i++ ){
            Post post2 = Post.builder().title("AAA"+ i).content("BBB"+i).build();
            post2.confirmWriter(newUser);
            postRepository.save(post2);
            Thread.sleep(50); // ???????????? ?????? ?????? ??????
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
     * ????????? ?????? ??????
     * ?????? ????????? ??????
     * */
    @Test
    public void ?????????_????????????_??????????????????() throws Exception {
        //given
        User newUser = userRepository.save(User.builder().username("newGen123").password("1q2w3e4r!!").nickname("??????").email("dldldl@d1ldl.dl").phoneNumber("000-1311-2222").role(Role.USER).build());

        final int POST_COUNT = 50;
        for(int i = 1; i<= POST_COUNT; i++ ){
            Post post = Post.builder().title("??????"+ i).content("??????"+i).build();
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