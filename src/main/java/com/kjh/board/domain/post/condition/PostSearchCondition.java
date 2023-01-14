package com.kjh.board.domain.post.condition;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "게시글 검색 조건")
public class PostSearchCondition {

    @Schema(description = "제목으로 검색")
    private String title;
    @Schema(description = "내용으로 검색")
    private String content;
}
