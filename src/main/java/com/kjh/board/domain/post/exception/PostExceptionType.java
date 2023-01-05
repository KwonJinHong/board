package com.kjh.board.domain.post.exception;

import com.kjh.board.global.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum PostExceptionType implements BaseExceptionType {

    POST_NOT_FOUND(700, HttpStatus.NOT_FOUND, "찾는 게시글이 없습니다"),
    NOT_AUTHORITY_UPDATE_POST(701, HttpStatus.FORBIDDEN, "게시글을 수정할 권한이 없습니다."),
    NOT_AUTHORITY_DELETE_POST(702, HttpStatus.FORBIDDEN, "게시글을 삭제할 권한이 없습니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    PostExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
