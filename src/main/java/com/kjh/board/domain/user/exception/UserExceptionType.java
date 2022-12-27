package com.kjh.board.domain.user.exception;

import com.kjh.board.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum UserExceptionType implements BaseExceptionType {
    //== 회원가입, 회원 조회 시 ==//
    ALREADY_EXIST_USERNAME(600, HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
    NOT_FOUND_USER(602, HttpStatus.NOT_FOUND, "회원 정보가 없습니다.");


    private int errorCode;

    private HttpStatus httpStatus;
    private String errorMessage;

    UserExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }
}
