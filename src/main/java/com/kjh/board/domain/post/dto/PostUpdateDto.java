package com.kjh.board.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateDto {

    private Optional<String> title = Optional.empty();
    private Optional<String> content = Optional.empty();
}
