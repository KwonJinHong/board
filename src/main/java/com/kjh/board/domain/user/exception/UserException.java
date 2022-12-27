package com.kjh.board.domain.user.exception;

import com.kjh.board.exception.BaseException;
import com.kjh.board.exception.BaseExceptionType;

public class UserException extends BaseException {

    private BaseExceptionType exceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
