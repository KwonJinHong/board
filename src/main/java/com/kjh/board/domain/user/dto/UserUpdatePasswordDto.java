package com.kjh.board.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 비밀번호 변경 DTO")
public class UserUpdatePasswordDto {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호", required = true)
    private String checkingPassword;

    @NotBlank(message = "바꿀 비밀번호를 입력해주세요.")
    @Pattern( regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,25}$",
            message = "8~25 자리의 알파벳, 숫자, 특수문자를 조합하여 비밀번호를 입력해주세요.")
    @Schema(description = "바꾸려는 비밀번호", required = true)
    private String changePassword;
}
