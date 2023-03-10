package com.kjh.board.service;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.comment.dto.CommentInfoDto;
import com.kjh.board.domain.comment.dto.CommentSaveDto;
import com.kjh.board.domain.comment.dto.CommentUpdateDto;
import com.kjh.board.domain.comment.exception.CommentException;
import com.kjh.board.domain.comment.exception.CommentExceptionType;
import com.kjh.board.domain.comment.service.CommentService;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.dto.PostSaveDto;
import com.kjh.board.domain.post.exception.PostException;
import com.kjh.board.domain.post.exception.PostExceptionType;
import com.kjh.board.domain.post.service.PostService;
import com.kjh.board.domain.comment.repository.CommentRepository;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.UserService;
import com.kjh.board.global.exception.BaseExceptionType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    CommentService commentService;

    @Autowired CommentRepository commentRepository;
    @Autowired UserService userService;
    @Autowired PostService postService;
    @Autowired PostRepository postRepository;

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
        userService.join(new UserJoinDto(USERNAME+"k3",PASSWORD+"1212","dngngn","zlglgl@zmzmz.com","000-2222-1111"));
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();
        emptyContext.setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        User.builder()
                                .username(USERNAME+"k3")
                                .password(PASSWORD+"1212")
                                .roles(Role.USER.toString())
                                .build(),
                        null)
        );
        SecurityContextHolder.setContext(emptyContext);
        clear();
    }

    private Long savePost(){
        String title = "??????";
        String content = "??????";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        Post save = postRepository.save(postSaveDto.toEntity());
        clear();
        return save.getId();
    }

    private Long saveComment(){
        Comment comment = Comment.builder().content("?????????").build();
        Long id = commentRepository.save(comment).getId();
        clear();
        return id;
    }

    private Long saveReComment(Long parentId){
        Comment parent = commentRepository.findById(parentId).orElse(null);
        Comment comment = Comment.builder().content("?????? ?????????").parent(parent).build();

        Long id = commentRepository.save(comment).getId();
        clear();
        return id;
    }

    @Test
    public void ????????????_??????() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");

        //when
        commentService.save(postId, commentSaveDto);
        clear();

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void ???????????????_??????() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");

        //when
        commentService.saveReComment(postId,parentId,commentSaveDto);
        clear();

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(2);

    }

    @Test
    public void ????????????_??????_????????????_??????() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");

        //when, then

        assertThat(assertThrows(PostException.class, () -> commentService.save(postId+123,commentSaveDto))
                .getExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);

    }

    @Test
    public void ???????????????_??????_????????????_??????() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");

        //when, then

        assertThat(assertThrows(PostException.class, () -> commentService.saveReComment(postId+123, parentId,commentSaveDto))
                .getExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);

    }

    @Test
    public void ???????????????_??????_?????????_??????() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");

        //when, then
        assertThat(assertThrows(CommentException.class, () -> commentService.saveReComment(postId, parentId+123,commentSaveDto))
                .getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);

    }

    @Test
    public void ????????????_??????() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);

        Long commentId = commentRepository.findAll().get(0).getId();
        CommentSaveDto reCommentSaveDto = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto);

        Long reCommentId = commentRepository.findAll().get(1).getId();

        clear();

        //when
        commentService.update(reCommentId, new CommentUpdateDto(Optional.of("??????")));
        clear();

        //then
        Comment comment = commentRepository.findById(reCommentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("??????");

    }

    @Test
    public void ????????????_??????_????????????() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);

        Long commentId = commentRepository.findAll().get(0).getId();
        CommentSaveDto reCommentSaveDto = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto);

        Long reCommentId = commentRepository.findAll().get(1).getId();

        clear();

        //when
        setAnotherAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.update(reCommentId, new CommentUpdateDto(Optional.of("????????????")))).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);


    }

    @Test
    public void ????????????_??????_?????????_??????() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);

        Long commentId = commentRepository.findAll().get(0).getId();
        CommentSaveDto reCommentSaveDto = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto);

        Long reCommentId = commentRepository.findAll().get(1).getId();

        clear();

        setAnotherAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.remove(reCommentId)).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
    }

    // ????????? ???????????????, ???????????? ???????????? ??????
    // DB??? ??????????????? ???????????? ??????, ????????? "????????? ???????????????"?????? ?????? -> DB??? ?????? ????????? ????????????.
    @Test
    public void ????????????_????????????_????????????_??????() throws Exception {

        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("????????????2");
        commentService.saveReComment(postId, commentId, reCommentSaveDto2);

        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))
                .getChildList().size()).isEqualTo(2);

        //when
        commentService.remove(commentId);
        clear();


        //then
        Comment findComment = commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));
        assertThat(findComment).isNotNull();
        assertThat(findComment.isRemoved()).isTrue();
        assertThat(findComment.getChildList().size()).isEqualTo(2);
    }

    // ???????????? ????????? ???????????? ?????? ????????? ???????????? ?????? -> ????????? DB?????? ????????????.
    @Test
    public void ????????????_????????????_??????_??????() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        //when
        commentService.remove(commentId);
        clear();

        //then
        Assertions.assertThat(commentRepository.findAll().size()).isSameAs(0);
        assertThat(assertThrows(CommentException.class, () ->commentRepository.findById(commentId).orElseThrow(()->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);
    }


    // ???????????? ??????????????? ?????? ????????? ????????? ???????????? ??????
    //????????? ????????? ?????? DB?????? ????????????. (??????????????? ?????? X)
    @Test
    public void ????????????_????????????_????????????_??????_?????????_????????????_??????() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("????????????2");
        commentService.saveReComment(postId, commentId, reCommentSaveDto2);
        Long reComment2Id = commentRepository.findAll().get(2).getId();


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(2);
        clear();

        commentService.remove(reComment1Id);
        clear();

        commentService.remove(reComment2Id);
        clear();

        Assertions.assertThat(commentRepository.findById(reComment1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reComment2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        clear();


        //when
        commentService.remove(commentId);
        clear();


        //then
        LongStream.rangeClosed(commentId, reComment2Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
                        .getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT)
        );

    }

    // ?????? ????????? ????????????, ???????????? ???????????? ??????
    // ????????? ??????, DB????????? ?????? X
    @Test
    public void ???????????????_???????????????_????????????_??????() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();


        //when
        commentService.remove(reComment1Id);
        clear();


        //then
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(reComment1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isFalse();
        Assertions.assertThat(commentRepository.findById(reComment1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
    }


    // ?????? ????????? ??????????????????, ??????????????? ?????? ????????? ??????
    // ????????? ????????? ?????? ???????????? DB?????? ?????? ??????, ?????????????????? ??????
    @Test
    public void ???????????????_???????????????_?????????_??????_??????_????????????_?????????_??????() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("????????????2");
        commentService.saveReComment(postId, commentId, reCommentSaveDto2);
        Long reComment2Id = commentRepository.findAll().get(2).getId();


        commentService.remove(reComment2Id);
        clear();
        commentService.remove(commentId);
        clear();


        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(2);

        //when
        commentService.remove(reComment1Id);



        //then
        LongStream.rangeClosed(commentId, reComment2Id).forEach(id ->
                assertThat(assertThrows(CommentException.class, () -> commentRepository.findById(id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)))
                        .getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT)
        );

    }


    // ?????? ????????? ??????????????????, ?????? ???????????? ?????? ???????????? ?????? ???????????? ??????
    //?????? ???????????? ??????, ????????? DB?????? ??????????????? ??????, ??????????????? "????????? ???????????????"?????? ??????
    @Test
    public void ???????????????_???????????????_?????????_??????_??????_????????????_????????????_??????() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("????????????");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("????????????");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("????????????2");
        commentService.saveReComment(postId, commentId, reCommentSaveDto2);
        Long reComment2Id = commentRepository.findAll().get(2).getId();


        commentService.remove(commentId);
        clear();

        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getChildList().size()).isEqualTo(2);


        //when
        commentService.remove(reComment2Id);
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();


        //then
        Assertions.assertThat(commentRepository.findById(reComment2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT))).isNotNull();
        Assertions.assertThat(commentRepository.findById(reComment2Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).isRemoved()).isTrue();
        Assertions.assertThat(commentRepository.findById(reComment1Id).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();
        Assertions.assertThat(commentRepository.findById(commentId).orElseThrow(()-> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)).getId()).isNotNull();

    }

}