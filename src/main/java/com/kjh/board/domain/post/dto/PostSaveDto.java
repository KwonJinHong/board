package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "게시글 저장 DTO")
public class PostSaveDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Schema(description = "게시글 제목")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    @Schema(description = "게시글 내용")
    private String content;

    public Post toEntity() {
        return Post.builder().title(title).content(content).build();
    }
}
