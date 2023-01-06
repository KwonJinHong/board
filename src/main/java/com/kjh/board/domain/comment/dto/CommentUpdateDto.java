package com.kjh.board.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Optional;

@Data
@AllArgsConstructor
public class CommentUpdateDto {

    private Optional<String> content;
}
