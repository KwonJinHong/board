package com.kjh.board.domain.user.controller;

import com.kjh.board.domain.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    /**
     * CREATE - 회원 등록
     * */
    /*@PostMapping("api/users")
    public ResponseEntity saveUserV1(@RequestBody @Valid UserDto.Request userDto) {
        userService.join(userDto);
        return ResponseEntity.ok("");
    }*/

    /**
     * Read - 회원 전체 목록 조회
     * */
    /*@GetMapping("api/users")
    public Result readUsersV1() {
        List<UserDto.Response> findUsers = userService.findAll();
        return new Result(findUsers);
    }*/

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
