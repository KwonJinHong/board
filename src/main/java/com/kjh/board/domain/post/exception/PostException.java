package com.kjh.board.domain.post.exception;

import com.kjh.board.global.exception.BaseException;
import com.kjh.board.global.exception.BaseExceptionType;

public class PostException extends BaseException {


    private BaseExceptionType exceptionType;

    public PostException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
