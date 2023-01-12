package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "사용자 정보")
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
