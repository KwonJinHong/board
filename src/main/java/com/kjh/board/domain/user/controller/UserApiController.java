package com.kjh.board.domain.user.controller;

import com.kjh.board.domain.user.dto.*;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.service.UserService;
import com.kjh.board.global.util.security.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    /**
     * CREATE - 회원 가입
     * */
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public void join(@Valid @RequestBody UserJoinDto userJoinDto) {
        userService.join(userJoinDto);
    }

    /**
     * Read - 회원 정보 조회
     * */
    @GetMapping("/user/{id}")
    public ResponseEntity getUserInfo(@Valid @PathVariable("id") Long id) {
        UserInfoDto userInfoDto = userService.getInfo(id);
        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    /**
     * Read - 내 정보 조회
     * */
    @GetMapping("/user")
    public ResponseEntity getMyInfo(HttpServletResponse response) {
        UserInfoDto userInfoDto = userService.getMyInfo();
        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    /**
     * Update - 회원 정보 수정
     * */
    @PutMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    public void updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        userService.update(SecurityUtil.getLoginUsername(), userUpdateDto);
    }

    /**
     * Update - 비밀번호 변경
     * */
    @PutMapping("/user/password")
    @ResponseStatus(HttpStatus.OK)
    public void updatePassword(@Valid @RequestBody UserUpdatePasswordDto userUpdatePasswordDto) throws Exception {
        userService.updatePassword(SecurityUtil.getLoginUsername(), userUpdatePasswordDto.getCheckingPassword(), userUpdatePasswordDto.getChangePassword());
    }

    /**
     * Delete - 회원탈퇴
     * */
    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void quit(@Valid @PathVariable Long id) throws Exception {
        userService.quit(id);
    }

}
