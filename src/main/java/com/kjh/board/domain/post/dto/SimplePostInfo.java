package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.post.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimplePostInfo {

    private Long postId;

    private String title;//제목
    private String content;//내용
    private String userName;//작성자의 이름
    private String createdDate; //작성일

    public SimplePostInfo(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.userName = post.getUser().getUsername();
        this.createdDate = post.getCreatedDate();
    }
}
