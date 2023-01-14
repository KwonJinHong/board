package com.kjh.board.domain.comment.dto;

import com.kjh.board.domain.comment.Comment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "댓글 저장 DTO")
public class CommentSaveDto {
    @Schema(description = "댓글 내용")
    private String content;

    public Comment toEntity() {
        return Comment.builder().content(content).build();
    }
}
