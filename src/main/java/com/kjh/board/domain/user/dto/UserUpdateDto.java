package com.kjh.board.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "사용자 정보 수정 DTO")
public class UserUpdateDto {

    @Schema(description = "사용자 닉네임")
    private Optional<String> nickname = Optional.empty();

    @Schema(description = "사용자 이메일")
    private Optional<String> email = Optional.empty();

    @Schema(description = "사용자 전화번호")
    private Optional<String> phoneNumber = Optional.empty();
}
