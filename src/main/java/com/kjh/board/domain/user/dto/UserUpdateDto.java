package com.kjh.board.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class UserUpdateDto {

    private final Optional<String> nickname;
    private final Optional<String> email;
    private final Optional<String> phoneNumber;
}
