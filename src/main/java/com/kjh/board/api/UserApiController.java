package com.kjh.board.api;

import com.kjh.board.domain.User;
import com.kjh.board.dto.UserDto;
import com.kjh.board.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    /**
     * CREATE - 회원 등록
     * */
    @PostMapping("api/v1/users")
    public ResponseEntity saveUserV1(@RequestBody @Valid UserDto userDto) {
        Long id = userService.join(userDto);
        return ResponseEntity.ok(id);
    }

    /**
     * Read - 회원 전체 목록 조회
     * */
    @GetMapping("api/v1/users")
    public Result readUsersV1() {
        List<UserDto> findUsers = userService.findAll();
        return new Result(findUsers);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }


}
