package com.kjh.board.dto;

import com.kjh.board.domain.Post;
import com.kjh.board.domain.User;
import lombok.Data;

@Data
public class PostDto {

    private Long id;
    private String title;
    private String content;

    public PostDto(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.title = post.getTitle();
    }

}
