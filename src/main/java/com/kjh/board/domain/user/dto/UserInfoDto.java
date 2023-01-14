package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "사용자 정보 조회 DTO")
public class UserInfoDto {

    @Schema(description = "사용자 ID")
    private String username;
    @Schema(description = "사용자 닉네임")
    private String nickname;
    @Schema(description = "사용자 이메일")
    private String email;
    @Schema(description = "사용자 전화번호")
    private String phoneNumber;

    @Builder
    public UserInfoDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }
}
