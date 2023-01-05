package com.kjh.board.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class PostUpdateDto {

    private Optional<String> title;
    private Optional<String> content;
}
