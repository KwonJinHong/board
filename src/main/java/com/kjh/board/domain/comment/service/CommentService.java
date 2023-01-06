package com.kjh.board.domain.comment.service;

import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.comment.dto.CommentSaveDto;
import com.kjh.board.domain.comment.dto.CommentUpdateDto;
import com.kjh.board.domain.comment.exception.CommentException;
import com.kjh.board.domain.comment.exception.CommentExceptionType;
import com.kjh.board.domain.post.Post;

import com.kjh.board.domain.comment.repository.CommentRepository;
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
    public Long save(Long id, CommentSaveDto commentSaveDto) {
        Comment comment = commentSaveDto.toEntity();

        comment.confirmWriter(userRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() ->
                new UserException(UserExceptionType.NOT_FOUND_USER)));

        comment.confirmPost(postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_FOUND)));

        commentRepository.save(comment);

        return comment.getId();
    }

    @Transactional
    public Long saveReComment(Long id, Long parentId,CommentSaveDto commentSaveDto) {
        Comment comment = commentSaveDto.toEntity();

        comment.confirmWriter(userRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(() ->
                new UserException(UserExceptionType.NOT_FOUND_USER)));

        comment.confirmPost(postRepository.findById(id).orElseThrow(() ->
                new PostException(PostExceptionType.POST_NOT_FOUND)));

        comment.confirmParent(commentRepository.findById(parentId).orElseThrow(()
                -> new CommentException(CommentExceptionType.NOT_FOUND_COMMENT)));

        commentRepository.save(comment);

        return comment.getId();
    }


    /**
     * Update - 댓글 내용 수정
     * 병합(merge)방식이 아닌 Dirty Checking 방식 사용
     * */
    @Transactional
    public void update(Long id, CommentUpdateDto commentUpdateDto) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
            new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        //유저 권한 검증
        checkAuthority(comment, CommentExceptionType.NOT_AUTHORITY_UPDATE_COMMENT);

        //댓글 내용 수정
        commentUpdateDto.getContent().ifPresent(comment::updateContent);
    }

    /**
     * Delete - 댓글 삭제
     * 댓글과 대댓글인 경우에 따라 판별하는 로직 -> findRemovableList()
     * */
    @Transactional
    public void remove(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new CommentException(CommentExceptionType.NOT_FOUND_COMMENT));

        //유저 권한 검증
        checkAuthority(comment, CommentExceptionType.NOT_AUTHORITY_DELETE_COMMENT);

        comment.remove();
        List<Comment> removableCommentList = comment.findRemovableList();
        commentRepository.deleteAll(removableCommentList);
    }

    private void checkAuthority(Comment comment, CommentExceptionType commentExceptionType) {
        if(!comment.getUser().getUsername().equals(SecurityUtil.getLoginUsername()))
            throw new CommentException(commentExceptionType);
    }


}
