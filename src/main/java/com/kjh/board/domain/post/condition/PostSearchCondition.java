package com.kjh.board.domain.post.condition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PostSearchCondition {

    private String title;
    private String content;
}
