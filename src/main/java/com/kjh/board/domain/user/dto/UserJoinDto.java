package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class UserJoinDto {

    private final String username;
    private final String password;
    private final String nickname;
    private final String email;
    private final String phonenumber;

    public User toEntity() {
        return User.builder().username(username).password(password).nickname(nickname).email(email).phonenumber(phonenumber).build();
    }
}
