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
    @ApiOperation(tags = "user", value = "회원 정보 조회", notes = "User의 Id로 해당 유저 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 데이터 형식"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "찾는 회원이 없음")
    })
    public ResponseEntity<UserInfoDto> getUserInfo(@Valid @PathVariable("id") Long id) {
        UserInfoDto userInfoDto = userService.getInfo(id);
        return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
    }

    /**
     * Read - 내 정보 조회
     * */
    @GetMapping("/user")
    @ApiOperation(tags = "user", value = "내 정보 조회", notes = "로그인 된 회원 자신의 정보를 조회합니다.")
    public ResponseEntity<UserInfoDto> getMyInfo(HttpServletResponse response) {
        UserInfoDto userInfoDto = userService.getMyInfo();
        return new ResponseEntity<>(userInfoDto, HttpStatus.OK);
    }

    /**
     * Update - 회원 정보 수정
     * */
    @PutMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "회원 정보 수정", notes = "닉네임, 이메일 주소, 전화번호 등을 바꾸거나 수정합니다.")
    public void updateUserInfo(@Valid @RequestBody UserUpdateDto userUpdateDto) {
        userService.update(SecurityUtil.getLoginUsername(), userUpdateDto);
    }

    /**
     * Update - 비밀번호 변경
     * */
    @PutMapping("/user/password")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "비밀번호 수정", notes = "비밀번호를 변경합니다. 현재 비밀번호가 필요합니다.")
    public void updatePassword(@Valid @RequestBody UserUpdatePasswordDto userUpdatePasswordDto) throws Exception {
        userService.updatePassword(SecurityUtil.getLoginUsername(), userUpdatePasswordDto.getCheckingPassword(), userUpdatePasswordDto.getChangePassword());
    }

    /**
     * Delete - 회원탈퇴
     * */
    @PostMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(tags = "user", value = "회원 탈퇴", notes = "회원 탈퇴 시, 현재 비밀번호가 필요합니다.")
    public void quit(@Valid @RequestBody UserQuitDto userQuitDto) throws Exception {
        userService.quit(SecurityUtil.getLoginUsername(), userQuitDto.getCheckingPassword());
    }

}
