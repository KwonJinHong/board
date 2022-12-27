package com.kjh.board.service;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.comment.service.CommentService;
import com.kjh.board.domain.post.service.PostService;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.comment.dto.CommentDto;
import com.kjh.board.domain.post.dto.PostDto;
import com.kjh.board.domain.comment.repository.CommentRepository;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    PostService postService;
    @Autowired PostRepository postRepository;
    @Autowired UserRepository userRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired
    CommentService commentService;
    @PersistenceContext EntityManager em;

    @Test
    public void 댓글_생성() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long postId = postService.save(postDto, user.getNickname());

        CommentDto.Request commentDto = CommentDto.Request.builder()
                .content("히히")
                .build();

        //when
        Long commentId = commentService.save(postId, user.getNickname(), commentDto);
        Comment comment = commentRepository.findById(commentId).get();

        //then
        assertEquals("dd", comment.getUser().getNickname(), "댓글 작성자 같은지 확인");
        assertEquals(postId, comment.getPost().getId(), "게시글 ID 같은지 확인");
        assertEquals("히히", comment.getContent(), "댓글 내용 같은지 확인");

    }

    @Test
    public void 댓글_조회() throws Exception {
        //given
        User user1 = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user1);

        User user2 = User.builder().username("jhk").nickname("gg").phonenumber("01097765644").email("dlglffks@zmfmfm.gnw").build();
        userRepository.save(user2);

        User user3 = User.builder().username("hsw").nickname("dlgl").phonenumber("01030806576").email("dlglffks@dndn.gnw").build();
        userRepository.save(user3);

        PostDto.Request postDto = PostDto.Request.builder()
                .title("이히1")
                .user(user1)
                .content("오홍")
                .build();

        Long postId = postService.save(postDto, user1.getNickname());

        CommentDto.Request commentDto1 = CommentDto.Request.builder()
                .content("히히1")
                .build();

        CommentDto.Request commentDto2 = CommentDto.Request.builder()
                .content("히히2")
                .build();

        CommentDto.Request commentDto3 = CommentDto.Request.builder()
                .content("히히3")
                .build();

        Long commentId1 = commentService.save(postId, user1.getNickname(), commentDto1);
        Long commentId2 = commentService.save(postId, user2.getNickname(), commentDto2);
        Long commentId3 = commentService.save(postId, user3.getNickname(), commentDto3);

        //when
        //해당 Id 게시글에 존재하는 모든 댓글 조회
        List<CommentDto.Response> all = commentService.findAll(postId);

        //then
        assertEquals(user1.getNickname(), all.get(0).getNickname(), "댓글1 작성자 같은지 확인");
        assertEquals(user2.getNickname(), all.get(1).getNickname(), "댓글2 작성자 같은지 확인");
        assertEquals(user3.getNickname(), all.get(2).getNickname(), "댓글3 작성자 같은지 확인");

        assertEquals(commentDto1.getContent(), all.get(0).getContent(), "댓글1 내용 같은지 확인");
        assertEquals(commentDto2.getContent(), all.get(1).getContent(), "댓글2 내용 같은지 확인");
        assertEquals(commentDto3.getContent(), all.get(2).getContent(), "댓글3 내용 같은지 확인");

    }

    @Test
    public void 댓글_수정() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long postId = postService.save(postDto, user.getNickname());

        CommentDto.Request commentDto = CommentDto.Request.builder()
                .content("히히")
                .build();

        CommentDto.Request update = CommentDto.Request.builder()
                .content("키키")
                .build();

        //when
        Long commentId = commentService.save(postId, user.getNickname(), commentDto);
        commentService.update(commentId, update);
        Comment comment = commentRepository.findById(commentId).get();

        //then
        assertEquals("키키", comment.getContent(), "댓글 내용 수정된 것 확인");
    }

    @Test
    public void 댓글_삭제() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long postId = postService.save(postDto, user.getNickname());

        CommentDto.Request commentDto = CommentDto.Request.builder()
                .content("히히")
                .build();

        //when
        Long commentId = commentService.save(postId, user.getNickname(), commentDto);
        commentService.delete(commentId);

        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> commentService.delete(commentId));
        assertEquals("해당 id의 댓글이 존재하지 않습니다. id: " + commentId, thrown.getMessage());

    }

}