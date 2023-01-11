package com.kjh.board.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.comment.dto.CommentSaveDto;
import com.kjh.board.domain.comment.exception.CommentException;
import com.kjh.board.domain.comment.exception.CommentExceptionType;
import com.kjh.board.domain.comment.repository.CommentRepository;
import com.kjh.board.domain.comment.service.CommentService;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.dto.PostSaveDto;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.UserService;
import com.kjh.board.global.jwt.service.JwtService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CommentApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    EntityManager em;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    JwtService jwtService;

    final String USERNAME = "username1";
    final String PASSWORD = "1q2w3e4r!!";

    private static User user;



    private void clear(){
        em.flush();
        em.clear();
    }


    @BeforeEach
    private void setUser() throws Exception {
        user = userRepository.save(User.builder().username(USERNAME).password(PASSWORD).nickname("졸린오후").email("dldldl@dldl.dl").phoneNumber("000-1111-2222").role(Role.USER).build());
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        //Spring Security에 관련되어 있어서 인증 정보를 미리 주입해주는 코드
        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(org.springframework.security.core.userdetails.User.builder()
                .username(USERNAME)
                .password(PASSWORD)
                .roles(Role.USER.name())
                .build(),
                null, null));

        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private String getAccessToken(){
        return jwtService.createAccessToken(USERNAME);
    }
    private String getNoAuthAccessToken(){
        return jwtService.createAccessToken(USERNAME+12);
    }

    private Long savePost(){
        String title = "제목";
        String content = "내용";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);


        //when
        Post save = postRepository.save(postSaveDto.toEntity());
        clear();
        return save.getId();
    }

    private Long saveComment(){
        CommentSaveDto commentSaveDto = new CommentSaveDto("댓글");
        commentService.save(savePost(),commentSaveDto);
        clear();

        //시간 순으로 가장 최근에 달린 댓글의 ID 반환
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    private Long saveReComment(Long parentId){

        CommentSaveDto commentSaveDto = new CommentSaveDto("대댓글");
        commentService.saveReComment(savePost(),parentId,commentSaveDto);
        clear();

        //시간 순으로 가장 최근에 달린 댓글의 ID 반환
        List<Comment> resultList = em.createQuery("select c from Comment c order by c.createdDate desc ", Comment.class).getResultList();
        return resultList.get(0).getId();
    }

    /**
     * 댓글 및 대댓글 저장
     * 댓글 저장 성공
     * */
    @Test
    public void 댓글저장() throws Exception {
        //given
        Long postId = savePost();

        Map<String, String> map = new HashMap<>();
        map.put("content", "댓글");
        String commentData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                post("/comment/" + postId)
                        .header("Authorization", "Bearer "+ getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentData))
                .andExpect(status().isCreated());

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(1);
    }

    /**
     * 댓글 및 대댓글 저장
     * 대댓글 저장 성공
     * */
    @Test
    public void 대댓글저장_성공() throws Exception {

        //given
        Long postId = savePost();
        Long parentId = saveComment();

        Map<String, String> map = new HashMap<>();
        map.put("content", "대댓글");
        String reCommentData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        post("/comment/"+postId+"/"+parentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reCommentData))
                .andExpect(status().isCreated());


        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(2);

    }

    /**
     * 댓글 및 대댓글 저장
     * 댓글 저장 실패 -> 게시글이 없음
     * */
    @Test
    public void 댓글저장_실패_게시글이_없음() throws Exception {
        //given
        Long postId = savePost();

        Map<String, String> map = new HashMap<>();
        map.put("content", "댓글");
        String commentData = objectMapper.writeValueAsString(map);

        //when, then
        mockMvc.perform(
                        post("/comment/" + 123)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(commentData))
                .andExpect(status().isNotFound());

    }

    /**
     * 댓글 및 대댓글 저장
     * 대댓글 저장 실패 -> 댓글이 없음
     * */
    @Test
    public void 대댓글저장_실패_댓글이_없음() throws Exception {

        //given
        Long postId = savePost();
        Long parentId = saveComment();

        Map<String, String> map = new HashMap<>();
        map.put("content", "대댓글");
        String reCommentData = objectMapper.writeValueAsString(map);


        //when, then
        mockMvc.perform(
                        post("/comment/"+postId+"/"+1000)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(reCommentData))
                .andExpect(status().isNotFound());

    }

    /**
     * 댓글 내용 업데이트
     * 업데이트 성공
     * */
    @Test
    public void 업데이트_성공() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        Map<String, String> map = new HashMap<>();
        map.put("content", "바뀐내용");
        String updateData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateData))
                .andExpect(status().isOk());


        //then
        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("바뀐내용");

    }

    /**
     * 댓글 내용 업데이트
     * 업데이트 실패 -> 권한이 없음
     * */
    @Test
    public void 업데이트_실패_권한없음() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        Map<String, String> map = new HashMap<>();
        map.put("content", "바뀐내용");
        String updateData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getNoAuthAccessToken()) // 권한이 없는 토큰 던져줌
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateData))
                .andExpect(status().isForbidden());


        //then
        Comment comment = commentRepository.findById(commentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("댓글");

    }

    /**
     * 댓글 및 대댓글 삭제 -> 댓글 삭제 경우
     * 댓글 삭제 성공
     * 대댓글이 아예 존재하지 않는 경우 -> 바로 DB에서도 삭제
     * */
    @Test
    public void 댓글삭제() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        //when
        mockMvc.perform(
                        delete("/comment/" + commentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(0);
        assertThat(assertThrows(CommentException.class, () ->commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);

    }

    /**
     * 댓글 및 대댓글 삭제 -> 댓글 삭제 경우
     * 대댓글이 남아 있는 경우
     * DB 에는 남아있고, 화면상에는 "삭제된 댓글입니다." 표시
     * */
    @Test
    public void 댓글삭제_대댓글이_남아있는_경우() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();
        for (int i = 0; i < 5; i++) {
            saveReComment(commentId);
            Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        }

        assertThat(commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(5);

        //when
        mockMvc.perform(
                        delete("/comment/" + commentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue(); // isRemoved 가 True 면 "삭제된 댓글입니다." 표시
        assertThat(findComment.getChildList().size()).isEqualTo(5);

    }

    /**
     * 댓글 및 대댓글 삭제 -> 댓글 삭제 경우
     * 대댓글이 있었으나 모두 삭제된 상태인 경우
     * 댓글과 대댓글 모두 DB 에서 일괄 삭제 -> 화면에서도 표시 X
     * */
    @Test
    public void 댓글삭제_대댓글이_모두_삭제되었을_경우() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        Long reCommentId1 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId2 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId3 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId4 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId5 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌

        assertThat(commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(5);

        commentService.remove(reCommentId1);
        clear();

        commentService.remove(reCommentId2);
        clear();

        commentService.remove(reCommentId3);
        clear();

        commentService.remove(reCommentId4);
        clear();

        commentService.remove(reCommentId5);
        clear();

        assertThat(commentRepository.findById(reCommentId1).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommentId2).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommentId3).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommentId4).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommentId5).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        clear();

        //when
        mockMvc.perform(
                        delete("/comment/" + commentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        LongStream.rangeClosed(commentId, reCommentId5).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()->
                        new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT)
        );

    }

    /**
     * 댓글 및 대댓글 삭제 -> 대댓글 삭제 경우
     * 부모 댓글이 남아 있는 경우
     * DB 에서 삭제되지 않고, 대댓글 내용만 "삭제된 댓글입니다." 표시
     * */
    @Test
    public void 대댓글삭제_부모댓글_남아있음() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();
        Long reCommentId = saveReComment(commentId);

        //when
        mockMvc.perform(
                        delete("/comment/" + reCommentId)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(reCommentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isFalse();
        assertThat(commentRepository.findById(reCommentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();

    }

    /**
     * 댓글 및 대댓글 삭제 -> 대댓글 삭제 경우
     * 부모 댓글이 삭제되고, 달려있는 대댓글들도 모두 삭제된 경우
     * 부모 댓글을 포함한 모든 대댓글들 DB에서 일괄 삭제, 화면 표시 X
     * */
    @Test
    public void 대댓글삭제_부모와_모든_대댓글_삭제() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        Long reCommentId1 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId2 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId3 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId4 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId5 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌

        assertThat(commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(5);

        commentService.remove(commentId);
        clear();

        commentService.remove(reCommentId1);
        clear();

        commentService.remove(reCommentId2);
        clear();

        commentService.remove(reCommentId3);
        clear();

        commentService.remove(reCommentId4);
        clear();

        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(5);

        //when
        mockMvc.perform(
                        delete("/comment/" + reCommentId5)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        LongStream.rangeClosed(commentId, reCommentId5).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()->
                        new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT)
        );

    }

    /**
     * 댓글 및 대댓글 삭제 -> 대댓글 삭제 경우
     * 부모 댓글이 삭제되었지만, 다른 대댓글은 남아 있는 경우
     * 해당 대댓글만 삭제되지만, DB에는 남아있고 화면상에 "삭제된 댓글입니다." 표시
     * */
    @Test
    public void 대댓글삭제_부모댓글이_삭제되었지만_다른_대댓글존재() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();

        Long reCommentId1 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId2 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌
        Long reCommentId3 = saveReComment(commentId);
        Thread.sleep(100); // 시간 순 정렬을 위해 텀을 줌

        commentService.remove(commentId);
        clear();

        commentService.remove(reCommentId1);
        clear();

        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(3);

        //when
        mockMvc.perform(
                        delete("/comment/" + reCommentId2)
                                .header("Authorization", "Bearer "+ getAccessToken())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();

        //then
        assertThat(commentRepository.findById(reCommentId1).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();
        assertThat(commentRepository.findById(reCommentId1).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommentId2).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        assertThat(commentRepository.findById(reCommentId2).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        assertThat(commentRepository.findById(reCommentId3).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();
        assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();

    }
    
    /**
     * 댓글 삭제 실패
     * 권한이 없음!!
     * */
    @Test
    public void 댓글삭제_실패_권한없음() throws Exception {
        //given
        Long postId = savePost();
        Long commentId = saveComment();
        
        //when
        mockMvc.perform(
                        delete("/comment/"+commentId)
                                .header("Authorization", "Bearer "+ getNoAuthAccessToken()) // 권한이 없는 토큰 던져줌
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(1);
    }

}