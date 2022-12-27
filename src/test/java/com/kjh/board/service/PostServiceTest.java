package com.kjh.board.service;

import com.kjh.board.domain.comment.service.CommentService;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.exception.PostException;
import com.kjh.board.domain.post.service.PostService;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.comment.dto.CommentDto;
import com.kjh.board.domain.post.dto.PostDto;
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
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired PostRepository postRepository;
    @Autowired
    CommentService commentService;

    PostException postException;

    @Autowired UserRepository userRepository;
    @PersistenceContext EntityManager em;



    @Test
    public void 게시글_저장() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        //when
        postService.save(postDto, user.getNickname());

        List<Post> postsList = postRepository.findAll();

        Post post = postsList.get(0);


        //then
        assertEquals("이히1", post.getTitle(), "저장된 게시글 아이디 확인");
        assertEquals("dd", post.getUser().getNickname(), "작성자 닉네임 확인");
        assertEquals("오홍", post.getContent(), "저장된 게시글 내용 확인");
    }

    @Test
    public void 게시글_전체_조회() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto1 = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        PostDto.Request postDto2 = PostDto.Request.builder()
                .title("이히2")
                .user(user)
                .content("오홍")
                .build();

        PostDto.Request postDto3 = PostDto.Request.builder()
                .title("이히3")
                .user(user)
                .content("오홍")
                .build();

        PostDto.Request postDto4 = PostDto.Request.builder()
                .title("이히4")
                .user(user)
                .content("오홍")
                .build();

        postService.save(postDto1, user.getNickname());
        postService.save(postDto2, user.getNickname());
        postService.save(postDto3, user.getNickname());
        postService.save(postDto4, user.getNickname());

        //when
        List<PostDto.Response> all = postService.findAll();

        //then
        assertEquals("이히1", all.get(0).getTitle(), "저장된 게시글 아이디 확인");
        assertEquals("이히2", all.get(1).getTitle(), "저장된 게시글 아이디 확인");
        assertEquals("이히3", all.get(2).getTitle(), "저장된 게시글 아이디 확인");
        assertEquals("이히4", all.get(3).getTitle(), "저장된 게시글 아이디 확인");

        assertEquals("dd", all.get(0).getUser().getNickname(), "작성자 닉네임 확인");
        assertEquals("dd", all.get(1).getUser().getNickname(), "작성자 닉네임 확인");
        assertEquals("dd", all.get(2).getUser().getNickname(), "작성자 닉네임 확인");
        assertEquals("dd", all.get(3).getUser().getNickname(), "작성자 닉네임 확인");

        assertEquals("오홍", all.get(0).getContent());
        assertEquals("오홍", all.get(1).getContent());
        assertEquals("오홍", all.get(2).getContent());
        assertEquals("오홍", all.get(3).getContent());

    }

    @Test
    public void ID로_게시글_조회() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto1 = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long id = postService.save(postDto1, user.getNickname());

        CommentDto.Request commentDto = CommentDto.Request.builder()
                .content("히히")
                .build();

        commentService.save(id, user.getNickname(), commentDto);


        //when
        PostDto.Response search = postService.findById(id);


        //then
        assertEquals("이히1", search.getTitle(), "ID로 찾은 게시글의 제목이 같은지");
        assertEquals("dd", search.getUser().getNickname(), "ID로 찾은 게시글의 작성자가 같은지");
        assertEquals("오홍", search.getContent(), "ID로 찾은 게시글의 내용이 같은지");

    }

    @Test
    public void 게시글_수정() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto1 = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long id = postService.save(postDto1, user.getNickname());

        //when

        PostDto.Request updateDto = PostDto.Request.builder()
                .title("히히")
                .content("호호")
                .build();

        postService.update(id, updateDto);
        PostDto.Response update = postService.findById(id);

        //then
        assertEquals("히히", update.getTitle());
        assertEquals("호호", update.getContent());

    }

    @Test
    public void 게시글_삭제() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto.Request postDto1 = PostDto.Request.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long id = postService.save(postDto1, user.getNickname());

        //when
        postService.delete(id);

        //then
        PostException thrown = assertThrows(PostException.class, () -> postService.findById(id));
        assertEquals("찾는 게시글이 없습니다", thrown.getExceptionType().getErrorMessage());


    }

}