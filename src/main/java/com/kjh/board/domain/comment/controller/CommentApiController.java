package com.kjh.board.domain.comment.controller;

import com.kjh.board.domain.comment.dto.CommentSaveDto;
import com.kjh.board.domain.comment.dto.CommentUpdateDto;
import com.kjh.board.domain.comment.dto.ReCommentInfoDto;
import com.kjh.board.domain.comment.service.CommentService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "comments", description = "댓글 API")
public class CommentApiController {

    private final CommentService commentService;

    /**
     * Create - 댓글 저장
     * */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/comment/{postId}")
    @ApiOperation(tags = "comments", value = "댓글 저장")
    public void commentSave(@PathVariable("postId") Long postId, @RequestBody CommentSaveDto commentSaveDto) {
        commentService.save(postId, commentSaveDto);
    }

    /**
     * Create - 대댓글 저장
     * */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/comment/{postId}/{commentId}")
    @ApiOperation(tags = "comments", value = "대댓글 저장")
    public void reCommentSave(@PathVariable("postId") Long postId,
                              @PathVariable("commentId") Long commentId, @RequestBody CommentSaveDto commentSaveDto) {
        commentService.saveReComment(postId, commentId, commentSaveDto);
    }

    /**
     * Update - 댓글 수정
     * */
    @PutMapping("/comment/{commentId}")
    @ApiOperation(tags = "comments", value = "댓글 수정")
    public void update(@PathVariable("commentId") Long commentId, @RequestBody CommentUpdateDto commentUpdateDto) {
        commentService.update(commentId, commentUpdateDto);
    }

    /**
     * Delete - 댓글 삭제
     * */
    @DeleteMapping("/comment/{commentId}")
    @ApiOperation(tags = "comments", value = "댓글 삭제")
    public void delete(@PathVariable("commentId") Long commentId) {
        commentService.remove(commentId);
    }



}
