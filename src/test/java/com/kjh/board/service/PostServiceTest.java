package com.kjh.board.service;

import com.kjh.board.domain.comment.service.CommentService;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.dto.PostInfoDto;
import com.kjh.board.domain.post.dto.PostSaveDto;
import com.kjh.board.domain.post.dto.PostUpdateDto;
import com.kjh.board.domain.post.exception.PostException;
import com.kjh.board.domain.post.exception.PostExceptionType;
import com.kjh.board.domain.post.service.PostService;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired PostRepository postRepository;
    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    PostException postException;

    @Autowired UserRepository userRepository;
    @PersistenceContext EntityManager em;

    private static final String USERNAME = "kjh1234";
    private static final String PASSWORD = "1q2w3e4r!!";


    private void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void joinAndSetAuthentication() throws Exception {
        userService.join(new UserJoinDto(USERNAME,PASSWORD,"zmfmfm","zmfmfm23@zmzmz.com","000-1111-1111"));

        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME)
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private void setAnotherAuthentication() throws Exception {
        userService.join(new UserJoinDto(USERNAME+"hjk321",PASSWORD,"dngngn","zlglgl@zmzmz.com","000-2222-1111"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME+"hjk321")
                                .password(PASSWORD)
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }



    @Test
    public void 게시글_저장_및_ID로_게시글_조회() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        //when
        postService.save(postSaveDto);
        clear();

        Long id = postRepository.findAll().get(0).getId();

        //then
        PostInfoDto findPost = postService.getPostInfo(id);
        assertThat(findPost.getPostId()).isEqualTo(id);
        assertThat(findPost.getTitle()).isEqualTo(title);
        assertThat(findPost.getContent()).isEqualTo(content);

    }

    @Test
    public void 게시글_저장_실패_제목이나_내용없음() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";

        PostSaveDto postSaveDto1 = new PostSaveDto(title, null);
        PostSaveDto postSaveDto2 = new PostSaveDto(null, content);

        int result = postRepository.findAll().size();

        //when, then
        assertThat(result).isEqualTo(0);


    }

    @Test
    public void 게시글_수정() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        String changeTitle = "안녕못해";
        String changeContent = "안먹었어";

        Long id = postRepository.findAll().get(0).getId();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.of(changeTitle), Optional.of(changeContent));
        postService.update(id, postUpdateDto);

        //then
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));
        assertThat(post.getTitle()).isEqualTo(changeTitle);
        assertThat(post.getContent()).isEqualTo(changeContent);

    }

    @Test
    public void 게시글_제목만_수정() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        String changeTitle = "안녕못해";

        Long id = postRepository.findAll().get(0).getId();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.of(changeTitle), Optional.empty());
        postService.update(id, postUpdateDto);

        //then
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));
        assertThat(post.getTitle()).isEqualTo(changeTitle);
        assertThat(post.getContent()).isEqualTo(content);

    }

    @Test
    public void 게시글_내용만_수정() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        String changeContent = "안먹었어";

        Long id = postRepository.findAll().get(0).getId();
        PostUpdateDto postUpdateDto = new PostUpdateDto(Optional.empty(), Optional.of(changeContent));
        postService.update(id, postUpdateDto);

        //then
        Post post = postRepository.findById(id).orElseThrow(() -> new PostException(PostExceptionType.POST_NOT_FOUND));
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(changeContent);

    }

    @Test
    public void 게시글_삭제() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        Long id = postRepository.findAll().get(0).getId();
        postService.delete(id);

        //then
        int result = postRepository.findAll().size();
        assertThat(result).isEqualTo(0);

    }

    @Test
    public void 게시글_삭제실패_권한이없음() throws Exception {
        //given
        String title = "안녕";
        String content = "저녁먹었니?";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        postService.save(postSaveDto);
        clear();

        //when
        Long id = postRepository.findAll().get(0).getId();

        //then
        setAnotherAuthentication();

        assertThrows(PostException.class, ()-> postService.delete(id));

    }

}