package com.kjh.board.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.parameters.P;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class UserUpdatePasswordDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private final String checkingPassword;

    @NotBlank(message = "바꿀 비밀번호를 입력해주세요.")
    @Pattern( regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,25}$",
            message = "8~25 자리의 알파벳, 숫자, 특수문자를 조합하여 비밀번호를 입력해주세요.")
    private final String changePassword;
}
