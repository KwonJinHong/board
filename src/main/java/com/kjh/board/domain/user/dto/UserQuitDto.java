package com.kjh.board.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 탈퇴 시 비밀번호 확인 DTO")
public class UserQuitDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호")
    private String checkingPassword;
}
