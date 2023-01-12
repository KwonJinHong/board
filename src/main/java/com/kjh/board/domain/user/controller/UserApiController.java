package com.kjh.board.domain.user.controller;

import com.kjh.board.domain.user.dto.*;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.service.UserService;
import com.kjh.board.global.util.security.SecurityUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "user", description = "사용자 API")
public class UserApiController {

    private final UserService userService;

    /**
     * CREATE - 회원 가입
     * */
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "회원가입", notes = "유저 정보를 입력받아 회원 가입을 합니다.")
    public void join(@Valid @RequestBody UserJoinDto userJoinDto) {
        userService.join(userJoinDto);
    }

    /**
     * Read - 회원 정보 조회
     * */
    @GetMapping("/user/{id}")
    @ApiOperation(tags = "user", value = "회원 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공", content = @Content(schema = @Schema(implementation = ResponseEntity.class)))
    })
    public ResponseEntity getUserInfo(@Valid @PathVariable("id") Long id) {
        UserInfoDto userInfoDto = userService.getInfo(id);
        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    /**
     * Read - 내 정보 조회
     * */
    @GetMapping("/user")
    @ApiOperation(tags = "user", value = "내 정보 조회")
    public ResponseEntity getMyInfo(HttpServletResponse response) {
        UserInfoDto userInfoDto = userService.getMyInfo();
        return new ResponseEntity(userInfoDto, HttpStatus.OK);
    }

    /**
     * Update - 회원 정보 수정
     * */
    @PutMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "회원 정보 수정")
    public void updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        userService.update(SecurityUtil.getLoginUsername(), userUpdateDto);
    }

    /**
     * Update - 비밀번호 변경
     * */
    @PutMapping("/user/password")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "비밀번호 수정")
    public void updatePassword(@Valid @RequestBody UserUpdatePasswordDto userUpdatePasswordDto) throws Exception {
        userService.updatePassword(SecurityUtil.getLoginUsername(), userUpdatePasswordDto.getCheckingPassword(), userUpdatePasswordDto.getChangePassword());
    }

    /**
     * Delete - 회원탈퇴
     * */
    @DeleteMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "회원 탈퇴")
    public void quit(@Valid @PathVariable Long id) throws Exception {
        userService.quit(id);
    }

}
