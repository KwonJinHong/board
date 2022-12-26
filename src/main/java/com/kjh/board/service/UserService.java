package com.kjh.board.service;

import com.kjh.board.domain.User;
import com.kjh.board.dto.UserDto;
import com.kjh.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public Long join(UserDto.Request userDto) {
        userRepository.save(userDto.toEntity());
        return userDto.getId();
    }

    /**
     * Read
     * 유저 전체 조회
     * */
    public List<UserDto.Response> findAll() {
        List<User> user = userRepository.findAll();
        //Entity -> DTO로 변환
        return user.stream().map(UserDto.Response::new).collect(Collectors.toList());
    }


}
