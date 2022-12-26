package com.kjh.board.service;

import com.kjh.board.domain.Post;
import com.kjh.board.domain.User;
import com.kjh.board.dto.PostDto;
import com.kjh.board.repository.PostRepository;
import com.kjh.board.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired PostService postService;

    @Autowired PostRepository postRepository;

    @Autowired UserRepository userRepository;
    @PersistenceContext EntityManager em;



    /*@Test
    public void 게시글_저장() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto postDto = PostDto.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        //when
        Long id = postService.save(postDto, user.getNickname());
        Post getPost = postService.findById(id).toEntity();

        //then
        System.out.println(postDto.getUser());
        assertEquals("이히1", getPost.getTitle(), "저장된 게시글 아이디 확인");
        assertEquals("dd", getPost.getUser().getNickname(), "작성자 닉네임 확인");
        assertEquals("오홍", getPost.getContent(), "저장된 게시글 내용 확인");
    }

    @Test
    public void 게시글_전체_조회() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto postDto1 = PostDto.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        PostDto postDto2 = PostDto.builder()
                .title("이히2")
                .user(user)
                .content("오홍")
                .build();

        PostDto postDto3 = PostDto.builder()
                .title("이히3")
                .user(user)
                .content("오홍")
                .build();

        PostDto postDto4 = PostDto.builder()
                .title("이히4")
                .user(user)
                .content("오홍")
                .build();

        Long saveId1 = postService.save(postDto1, user.getNickname());
        Long saveId2 = postService.save(postDto2, user.getNickname());
        Long saveId3 = postService.save(postDto3, user.getNickname());
        Long saveId4 = postService.save(postDto4, user.getNickname());

        //when

        Post getPost1 = postService.findById(saveId1).toEntity();
        Post getPost2 = postService.findById(saveId2).toEntity();
        Post getPost3 = postService.findById(saveId3).toEntity();
        Post getPost4 = postService.findById(saveId4).toEntity();

        List<PostDto> all = postService.findAll();

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

        PostDto postDto1 = PostDto.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long id = postService.save(postDto1, user.getNickname());

        //when
        Post search = postService.findById(id).toEntity();

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

        PostDto postDto1 = PostDto.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long id = postService.save(postDto1, user.getNickname());

        //when

        PostDto updateDto = PostDto.builder()
                .title("히히")
                .content("호호")
                .build();

        postService.update(id, updateDto);
        Post update = postService.findById(id).toEntity();

        //then
        assertEquals("히히", update.getTitle());
        assertEquals("호호", update.getContent());

    }

    @Test
    public void 게시글_삭제() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("dd").phonenumber("01090765644").email("dlgl@zmfmfm.gnw").build();
        userRepository.save(user);

        PostDto postDto1 = PostDto.builder()
                .title("이히1")
                .user(user)
                .content("오홍")
                .build();

        Long id = postService.save(postDto1, user.getNickname());

        //when
        postService.delete(id);

        //then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> postService.findById(id));
        assertEquals("해당 id의 게시글이 존재하지 않습니다. id: " + id, thrown.getMessage());


    }*/

}