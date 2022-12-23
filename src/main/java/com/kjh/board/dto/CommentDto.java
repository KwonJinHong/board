package com.kjh.board.dto;

import com.kjh.board.domain.Comment;
import lombok.Data;

@Data
public class CommentDto {

    private Long id;
    private String content;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
    }
}
