package com.kjh.board.service;

import com.kjh.board.domain.Comment;
import com.kjh.board.domain.Post;
import com.kjh.board.domain.User;
import com.kjh.board.dto.CommentDto;
import com.kjh.board.repository.CommentRepository;
import com.kjh.board.repository.PostRepository;
import com.kjh.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * Create - 댓글 달기
     * */
    @Transactional
    public Long save(Long id, String nickname, CommentDto.Request commentDto) {
        Comment comment = commentDto.toEntity();

        comment.confirmWriter(userRepository.findByNickname(nickname));
        comment.confirmPost(postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("댓글 달기 실패: 해당 ID의 게시글이 존재하지 않습니다. ID : " + id)));

        commentRepository.save(comment);

        return comment.getId();
    }

    /**
     * Read - 댓글 리스트 조회
     * */
    public List<CommentDto.Response> findAll(Long id) {
        //게시글 ID로 해당 게시글을 찾아옴
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 id의 게시글이 존재하지 않습니다. id: " + id));

        //해당 게시글에 있는 댓글 리스트를 가져옴
        List<Comment> comments = post.getComments();
        //Entity를 DTO로 변환하여 반환
        return comments.stream().map(CommentDto.Response::new).collect(Collectors.toList());
    }

    /**
     * Update - 댓글 내용 수정
     * 병합(merge)방식이 아닌 Dirty Checking 방식 사용
     * */
    @Transactional
    public void update(Long id, CommentDto.Request commentDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
            new IllegalArgumentException("해당 id의 댓글이 존재하지 않습니다. id: " + id));

        //댓글 내용 수정
        comment.update(commentDto.getContent());
    }

    /**
     * Delete - 댓글 삭제
     * */
    @Transactional
    public void delete(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 id의 댓글이 존재하지 않습니다. id: " + id));

        commentRepository.delete(comment);
    }

}
