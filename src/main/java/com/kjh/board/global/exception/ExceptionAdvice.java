package com.kjh.board.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {


    //HttpMessageNotReadableException  => json 파싱 오류

    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseEx(BaseException exception){

        return new ResponseEntity(new ExceptionDto(exception.getExceptionType().getErrorCode()),exception.getExceptionType().getHttpStatus());
    }



    //@Valid 에서 예외 발생
    @ExceptionHandler(BindException.class)
    public ResponseEntity handleValidEx(BindException exception){

        return new ResponseEntity(new ExceptionDto(2000),HttpStatus.BAD_REQUEST);
    }

    //HttpMessageNotReadableException  => json 파싱 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity httpMessageNotReadableExceptionEx(HttpMessageNotReadableException exception){

        return new ResponseEntity(new ExceptionDto(3000),HttpStatus.BAD_REQUEST);
    }






    @ExceptionHandler(Exception.class)
    public ResponseEntity handleMemberEx(Exception exception) {


        exception.printStackTrace();
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }


    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
    }
}
