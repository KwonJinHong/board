package com.kjh.board.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    private Optional<String> nickname = Optional.empty();
    private Optional<String> email = Optional.empty();
    private Optional<String> phoneNumber = Optional.empty();
}
