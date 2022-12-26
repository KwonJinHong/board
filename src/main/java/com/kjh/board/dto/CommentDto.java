package com.kjh.board.dto;

import com.kjh.board.domain.Comment;
import com.kjh.board.domain.Post;
import com.kjh.board.domain.User;
import lombok.*;

/**
 * Response와 Request DTO 클래스를 Inner Static 클래스로 한번에 묶어서 관리
 * Response DTO와 Request DTO가 서로 필요한 속성이 달라 분리를 결정
 * */
public class CommentDto {

    /**
     *  댓글 Service 요청을 위한 DTO 클래스
     * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        private Long id;
        private User user;
        private String content;
        private Post post;

        //DTO -> Entity로 변환
        public Comment toEntity() {
            Comment comment = Comment.builder()
                    .id(id)
                    .user(user)
                    .content(content)
                    .post(post)
                    .build();

            return comment;
        }

    }

    /**
     * 댓글 정보를 리턴할 응답(Response) 클래스
     * Entity 클래스를 생성자 파라미터로 받아 데이터를 Dto로 변환하여 응답
     * 별도의 전달 객체를 활용해 연관관계를 맺은 엔티티간의 무한참조를 방지
     */
    @RequiredArgsConstructor
    @Getter
    public static class Response {
        private Long id;
        private String content;
        private String createdDate;
        private String modifiedDate;
        private String nickname;
        private Long userId;
        private Long postsId;

        /* Entity -> Dto*/
        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.createdDate = comment.getCreatedDate();
            this.modifiedDate = comment.getModifiedDate();
            this.nickname = comment.getUser().getNickname();
            this.userId = comment.getUser().getId();
            this.postsId = comment.getPost().getId();
        }
    }
}
