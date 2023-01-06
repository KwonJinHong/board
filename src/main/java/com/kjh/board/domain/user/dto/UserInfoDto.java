package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import lombok.Builder;
import lombok.Data;

@Data
public class UserInfoDto {

    private String username;
    private String nickname;
    private String email;
    private String phoneNumber;

    @Builder
    public UserInfoDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }
}
