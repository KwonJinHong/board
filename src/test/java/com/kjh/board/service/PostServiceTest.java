package com.kjh.board.service;

import com.kjh.board.domain.User;
import com.kjh.board.dto.PostDto;
import com.kjh.board.repository.PostRepository;
import com.kjh.board.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class PostServiceTest {

    @Autowired PostService postService;

    @Autowired PostRepository postRepository;

    @Autowired UserRepository userRepository;
    @PersistenceContext EntityManager em;



    @Test
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
        postService.save(postDto, user.getNickname());

        //then
        //System.out.println(String.valueOf(postDto));

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

        postService.save(postDto1, user.getNickname());
        postService.save(postDto2, user.getNickname());
        postService.save(postDto3, user.getNickname());
        postService.save(postDto4, user.getNickname());

        //when

        List<PostDto> all = postService.findAll();

        //then
        for (PostDto postDto : all) {
            System.out.println(postDto.getTitle());
        }

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
        PostDto search = postService.findById(id);

        //then
        System.out.println("--------");
        System.out.println(search.getTitle());
    }

}