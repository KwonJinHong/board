package com.kjh.board.dto;

import com.kjh.board.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String username;
    private String nickname;
    private String phonenumber;
    private String email;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.phonenumber = user.getPhonenumber();
        this.email = user.getEmail();
    }

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
