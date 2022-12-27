package com.kjh.board.domain.user.service;

import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.dto.UserDto;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.exception.UserExceptionType;
import com.kjh.board.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create
     * 유저 등록
     * */
    @Transactional
    public void join(UserDto.Request userDto) {
        userRepository.save(userDto.toEntity());
    }

    /**
     * Read
     * 유저 정보 가져오기
     * */
    public UserDto.Response getInfo(Long id) {
        User findUser = userRepository.findById(id).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));
        //Entity -> DTO로 변환
        return new UserDto.Response(findUser);
    }


}
