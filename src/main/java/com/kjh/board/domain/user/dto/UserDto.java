package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import lombok.*;

/**
 * Response와 Request DTO 클래스를 Inner Static 클래스로 한번에 묶어서 관리
 * Response DTO와 Request DTO가 서로 필요한 속성이 달라 분리를 결정
 * */
public class UserDto {

    /**
     * 회원 Service 요청(Request) DTO 클래스
     * */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        private Long id;
        private String username;
        private String nickname;
        private String phonenumber;
        private String email;

        //DTO -> Entity로 변환
        public User toEntity() {
            User user = User.builder()
                    .id(id)
                    .username(username)
                    .nickname(nickname)
                    .phonenumber(phonenumber)
                    .email(email)
                    .build();
            return user;
        }
    }
    /*


     */
    @Getter
    public static class Response {
        private final Long id;
        private final String username;
        private final String nickname;
        private final String phonenumber;
        private final String email;

        public Response(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.phonenumber = user.getPhonenumber();
            this.email = user.getEmail();
        }
    }


}
