package com.kjh.board.domain.comment.dto;

import com.kjh.board.domain.comment.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CommentSaveDto {

    private String content;

    public Comment toEntity() {
        return Comment.builder().content(content).build();
    }
}
