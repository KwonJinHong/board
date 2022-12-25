package com.kjh.board.dto;

import com.kjh.board.domain.Post;
import com.kjh.board.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private User user;
    private String title;
    private String content;
    private String createdDate;
    private String modifiedDate;

    public PostDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.user = post.getUser();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.modifiedDate = post.getModifiedDate();
    }

    /* Dto -> Entity */
    public Post toEntity() {
        Post post = Post.builder()
                .id(id)
                .user(user)
                .title(title)
                .content(content)
                .build();

        return post;
    }

}
