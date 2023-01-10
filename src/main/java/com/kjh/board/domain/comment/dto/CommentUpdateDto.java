package com.kjh.board.domain.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentUpdateDto {

    private Optional<String> content = Optional.empty();
}
