package com.kjh.board.domain.post.dto;

import com.kjh.board.domain.comment.dto.CommentDto;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.user.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Response와 Request DTO 클래스를 Inner Static 클래스로 한번에 묶어서 관리
 * Response DTO와 Request DTO가 서로 필요한 속성이 달라 분리를 결정
 * */
public class PostDto {

    /**
     * 게시글의 등록과 수정을 처리할 요청(Request) 클래스
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        private Long id;
        private User user;
        private String title;
        private String content;
        private String createdDate;
        private String modifiedDate;

        //DTO -> Entity로 변환
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

    /**
     * 게시글 정보를 리턴할 응답(Response) 클래스
     * Entity 클래스를 생성자 파라미터로 받아 데이터를 Dto로 변환하여 응답
     * 별도의 전달 객체를 활용해 연관관계를 맺은 엔티티간의 무한참조를 방지
     */
    @Getter
    public static class Response {

        private final Long id;
        private final String title;
        private final User user;
        private final String content;
        private final String createdDate, modifiedDate;
        private final List<CommentDto.Response> comments;

        public Response(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.user = post.getUser();
            this.content = post.getContent();
            this.createdDate = post.getCreatedDate();
            this.modifiedDate = post.getModifiedDate();
            this.comments = post.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList());
        }
    }
}







