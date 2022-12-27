package com.kjh.board.domain.post.service;

import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.exception.PostException;
import com.kjh.board.domain.post.exception.PostExceptionType;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.post.dto.PostDto;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Long save(PostDto.Request postDto, String nickname) {
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
    public List<PostDto.Response> findAll() {
        List<Post> post = postRepository.findAll();
        //Entity -> DTO로 변환
        return post.stream().map(PostDto.Response::new).collect(Collectors.toList());
    }

    /**
     * Read - 게시글 리스트 조회
     * 단건 조회
     * */
    public PostDto.Response findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_POUND));

        return new PostDto.Response(post);
    }

    /**
     * UPDATE - 게시글 수정
     * 병합(merge)방식이 아닌 Dirty Checking 방식 사용
     * */
    @Transactional
    public void update(Long id, PostDto.Request postDto) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_POUND));

        post.update(postDto.getTitle(), postDto.getContent());
    }

    /**
     * Delete - 게시글 삭제
     * */
    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_POUND));

        postRepository.delete(post);
    }
}
