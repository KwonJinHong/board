package com.kjh.board.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 수정 DTO")
public class PostUpdateDto {

    @Schema(description = "수정할 게시글 제목")
    private Optional<String> title = Optional.empty();
    @Schema(description = "수정할 게시글 내용")
    private Optional<String> content = Optional.empty();
}
