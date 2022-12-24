package com.kjh.board.service;

import com.kjh.board.domain.Post;
import com.kjh.board.domain.User;
import com.kjh.board.dto.PostDto;
import com.kjh.board.repository.PostRepository;
import com.kjh.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;


    /**
     * Create - 게시물 저장
     * */
    @Transactional
    public Long save(PostDto postDto, String nickname) {
        //닉네임으로 유저 정보 가져와 postDto에 넘겨준다.
        User user = userRepository.findByNickname(nickname);
        postDto.setUser(user);

        Post post = postDto.toEntity();
        postRepository.save(post);

        return post.getId();
    }

    /**
     * Read - 게시글 리스트 조회
     * 전체조회
     * */
    public List<PostDto> findAll() {
        List<Post> post = postRepository.findAll();
        //Entity -> DTO로 변환
        return post.stream().map(PostDto::new).collect(Collectors.toList());
    }

    /**
     * Read - 게시글 리스트 조회
     * 단건 조회
     * */
    public PostDto findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 id의 게시글이 존재하지 않습니다. id: " + id));

        return new PostDto(post);
    }
}
