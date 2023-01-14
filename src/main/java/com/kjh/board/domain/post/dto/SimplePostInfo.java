package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.post.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "간략한 게시글 정보 DTO")
public class SimplePostInfo {

    @Schema(description = "게시글 ID")
    private Long postId;
    @Schema(description = "게시글 제목")
    private String title;//제목
    @Schema(description = "게시글 내용")
    private String content;//내용
    @Schema(description = "게시글 작성자 ID")
    private String username;//작성자의 ID
    @Schema(description = "게시글 작성일")
    private String createdDate; //작성일

    public SimplePostInfo(Post post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.username = post.getUser().getUsername();
        this.createdDate = post.getCreatedDate().toString();
    }
}
