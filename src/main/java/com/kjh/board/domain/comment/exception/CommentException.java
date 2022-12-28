package com.kjh.board.domain.comment.exception;

import com.kjh.board.global.exception.BaseException;
import com.kjh.board.global.exception.BaseExceptionType;

public class CommentException extends BaseException {

    private BaseExceptionType exceptionType;

    public CommentException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
