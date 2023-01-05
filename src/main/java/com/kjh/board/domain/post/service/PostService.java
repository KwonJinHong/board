package com.kjh.board.domain.post.service;

import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.dto.PostInfoDto;
import com.kjh.board.domain.post.dto.PostSaveDto;
import com.kjh.board.domain.post.dto.PostUpdateDto;
import com.kjh.board.domain.post.exception.PostException;
import com.kjh.board.domain.post.exception.PostExceptionType;
import com.kjh.board.domain.post.repository.PostRepository;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.exception.UserExceptionType;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void save(PostSaveDto postSaveDto) {

      Post post = postSaveDto.toEntity();

      post.confirmWriter(userRepository.findByUsername(SecurityUtil.getLoginUsername())
              .orElseThrow(()-> new UserException(UserExceptionType.NOT_FOUND_USER)));

      postRepository.save(post);

    }


    /**
     * Read - 게시글 리스트 조회
     * 단건 조회
     * */
    public PostInfoDto getPostInfo(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_FOUND));

        return new PostInfoDto(post);
    }

    /**
     * UPDATE - 게시글 수정
     * 병합(merge)방식이 아닌 Dirty Checking 방식 사용
     * */
    @Transactional
    public void update(Long id, PostUpdateDto postUpdateDto) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_FOUND));

        checkAuthority(post,PostExceptionType.NOT_AUTHORITY_UPDATE_POST );

        postUpdateDto.getTitle().ifPresent(post::updateTitle);
        postUpdateDto.getContent().ifPresent(post::updateContent);
    }

    /**
     * Delete - 게시글 삭제
     * */
    @Transactional
    public void delete(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_FOUND));

        checkAuthority(post,PostExceptionType.NOT_AUTHORITY_DELETE_POST );

        postRepository.delete(post);
    }


    //게시글에 대한 수정, 삭제 권한을 검사함
    private void checkAuthority(Post post, PostExceptionType postExceptionType) {
        if(!post.getUser().getUsername().equals(SecurityUtil.getLoginUsername()))
            throw new PostException(postExceptionType);
    }
}
