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
        String title = "안녕";
        String content = "하이";
        PostSaveDto postSaveDto = new PostSaveDto(title, content);

        Post save = postRepository.save(postSaveDto.toEntity());
        clear();
        return save.getId();
    }

    private Long saveComment(){
        Comment comment = Comment.builder().content("조와용").build();
        Long id = commentRepository.save(comment).getId();
        clear();
        return id;
    }

    private Long saveReComment(Long parentId){
        Comment parent = commentRepository.findById(parentId).orElse(null);
        Comment comment = Comment.builder().content("나도 조와용").parent(parent).build();

        Long id = commentRepository.save(comment).getId();
        clear();
        return id;
    }

    @Test
    public void 댓글저장_성공() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");

        //when
        commentService.save(postId, commentSaveDto);
        clear();

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(1);
    }

    @Test
    public void 대댓글저장_성공() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("안뇽안뇽");

        //when
        commentService.saveReComment(postId,parentId,commentSaveDto);
        clear();

        //then
        int result = commentRepository.findAll().size();
        assertThat(result).isEqualTo(2);

    }

    @Test
    public void 댓글저장_실패_게시글이_없음() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");

        //when, then

        assertThat(assertThrows(PostException.class, () -> commentService.save(postId+123,commentSaveDto))
                .getExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);

    }

    @Test
    public void 대댓글저장_실패_게시글이_없음() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("안뇽안뇽");

        //when, then

        assertThat(assertThrows(PostException.class, () -> commentService.saveReComment(postId+123, parentId,commentSaveDto))
                .getExceptionType()).isEqualTo(PostExceptionType.POST_NOT_FOUND);

    }

    @Test
    public void 대댓글저장_실패_댓글이_없음() throws Exception {
        //given
        Long postId = savePost();
        Long parentId = saveComment();
        CommentSaveDto commentSaveDto = new CommentSaveDto("안뇽안뇽");

        //when, then
        assertThat(assertThrows(CommentException.class, () -> commentService.saveReComment(postId, parentId+123,commentSaveDto))
                .getExceptionType()).isEqualTo(CommentExceptionType.NOT_FOUND_COMMENT);

    }

    @Test
    public void 업데이트_성공() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);

        Long commentId = commentRepository.findAll().get(0).getId();
        CommentSaveDto reCommentSaveDto = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto);

        Long reCommentId = commentRepository.findAll().get(1).getId();

        clear();

        //when
        commentService.update(reCommentId, new CommentUpdateDto(Optional.of("키키")));
        clear();

        //then
        Comment comment = commentRepository.findById(reCommentId).orElse(null);
        assertThat(comment.getContent()).isEqualTo("키키");

    }

    @Test
    public void 업데이트_실패_권한없음() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);

        Long commentId = commentRepository.findAll().get(0).getId();
        CommentSaveDto reCommentSaveDto = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto);

        Long reCommentId = commentRepository.findAll().get(1).getId();

        clear();

        //when
        setAnotherAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.update(reCommentId, new CommentUpdateDto(Optional.of("업데이트")))).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);


    }

    @Test
    public void 댓글삭제_실패_권한이_없음() throws Exception {
        //given
        Long postId = savePost();
        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);

        Long commentId = commentRepository.findAll().get(0).getId();
        CommentSaveDto reCommentSaveDto = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto);

        Long reCommentId = commentRepository.findAll().get(1).getId();

        clear();

        setAnotherAuthentication();

        //when, then
        BaseExceptionType type = assertThrows(CommentException.class, () -> commentService.remove(reCommentId)).getExceptionType();
        assertThat(type).isEqualTo(CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);
    }

    // 댓글을 삭제하지만, 대댓글은 남아있는 경우
    // DB와 화면에서는 지워지지 않고, 화면엔 "삭제된 댓글입니다"라고 표시 -> DB엔 원래 내용이 남아있다.
    @Test
    public void 댓글삭제_대댓글이_남아있는_경우() throws Exception {

        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("안뇽안뇽2");
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

    // 대댓글이 하나도 존재하지 않는 댓글을 삭제하는 경우 -> 곧바로 DB에서 삭제한다.
    @Test
    public void 댓글삭제_대댓글이_없는_경우() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
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


    // 대댓글이 존재했으나 모두 삭제된 댓글을 삭제하는 경우
    //댓글과 대댓글 모두 DB에서 삭제된다. (화면상에도 표시 X)
    @Test
    public void 댓글삭제_대댓글이_존재하나_모두_삭제된_대댓글인_경우() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("안뇽안뇽2");
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

    // 부모 댓글이 남아있고, 대댓글만 삭제하는 경우
    // 내용만 삭제, DB에서는 삭제 X
    @Test
    public void 대댓글삭제_부모댓글이_남아있는_경우() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("안뇽안뇽");
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


    // 부모 댓글이 삭제되어있고, 대댓글들도 모두 삭제된 경우
    // 부모를 포함한 모든 대댓글을 DB에서 일괄 삭제, 화면상에서도 지움
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_모든_대댓글이_삭제된_경우() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("안뇽안뇽2");
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


    // 부모 댓글이 삭제되어있고, 다른 대댓글이 아직 삭제되지 않고 남아있는 경우
    //해당 대댓글만 삭제, 그러나 DB에서 삭제되지는 않고, 화면상에는 "삭제된 댓글입니다"라고 표시
    @Test
    public void 대댓글삭제_부모댓글이_삭제된_경우_다른_대댓글이_남아있는_경우() throws Exception {
        //given
        Long postId = savePost();

        CommentSaveDto commentSaveDto = new CommentSaveDto("하이하이");
        commentService.save(postId, commentSaveDto);
        Long commentId = commentRepository.findAll().get(0).getId();

        CommentSaveDto reCommentSaveDto1 = new CommentSaveDto("안뇽안뇽");
        commentService.saveReComment(postId, commentId, reCommentSaveDto1);
        Long reComment1Id = commentRepository.findAll().get(1).getId();

        CommentSaveDto reCommentSaveDto2 = new CommentSaveDto("안뇽안뇽2");
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