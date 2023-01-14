package com.kjh.board.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "댓글 내용 수정 DTO")
public class CommentUpdateDto {

    @Schema(description = "변경할 댓글 내용")
    private Optional<String> content = Optional.empty();
}
