package com.kjh.board.domain.user.dto;

import com.kjh.board.domain.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Schema(description = "사용자 회원 가입 정보")
public class UserJoinDto {

    @NotBlank
    @Size(min = 6, max = 20, message = "6~20자 내외로 아이디를 입력해주세요.")
    @Schema(description = "사용자 ID",  required = true)
    private String username;
    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,25}$",
            message = "8~25 자리의 알파벳, 숫자, 특수문자를 조합하여 비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", required = true)
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임이 너무 짧습니다.")
    @Schema(description = "사용자 닉네임",  required = true)
   private String nickname;
    @NotBlank
    @Pattern(regexp = "^[_a-z\\d-]+(.[_a-z\\d-]+)*@(?:\\w+\\.)+\\w+$",
            message = "이메일 형식으로 입력해 주세요.")
    @Schema(description = "이메일", example = "abc@jiniworld.me",  required = true)
    private String email;
    @NotBlank
    @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}",
            message = "-을 포함하여 전화번호를 입력해주세요.")
    @Schema(description = "전화번호", example = "000-0000-0000",  required = true)
    private String phoneNumber;

    public User toEntity() {
        return User.builder().username(username).password(password).nickname(nickname).email(email).phoneNumber(phoneNumber).build();
    }
}
