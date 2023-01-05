package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostInfoDto {

    private Long postId;
    private String title;
    private String content;

    public PostInfoDto(Post post) {

        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();

    }
}
