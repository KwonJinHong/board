package com.kjh.board.service;

import com.kjh.board.domain.User;
import com.kjh.board.dto.UserDto;
import com.kjh.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public Long join(UserDto userdto) {
        userRepository.save(userdto.toEntity());
        return userdto.getId();
    }


}
