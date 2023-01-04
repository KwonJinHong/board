package com.kjh.board.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserQuitDto {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private final String checkingPassword;
}
