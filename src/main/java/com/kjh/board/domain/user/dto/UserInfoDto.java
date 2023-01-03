package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import lombok.Builder;
import lombok.Data;

@Data
public class UserInfoDto {

    private final String username;
    private final String nickname;
    private final String email;
    private final String phonenumber;

    @Builder
    public UserInfoDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phonenumber = user.getPhonenumber();
    }
}
