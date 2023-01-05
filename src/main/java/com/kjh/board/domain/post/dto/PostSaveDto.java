package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.post.Post;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class PostSaveDto {

    @NotBlank(message = "제목을 입력해주세요.")
    private final String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private final String content;

    public Post toEntity() {
        return Post.builder().title(title).content(content).build();
    }
}
