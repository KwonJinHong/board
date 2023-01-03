package com.kjh.board.domain.user.service;

import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.dto.UserDto;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.exception.UserExceptionType;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create
     * 유저 등록
     * */
    @Transactional
    public void join(UserDto.Request userDto) {

        User user = userDto.toEntity();
        user.addUserAuthority();
        user.encodePassword(passwordEncoder);

        if(userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new UserException(UserExceptionType.ALREADY_EXIST_USERNAME);
        }

        userRepository.save(user);
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

    /**
     * Read
     * 내 정보 가져오기
     * */
    public UserDto.Response getMyInfo() {
        User findUser = userRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));
        //Entity -> DTO로 변환
        return new UserDto.Response(findUser);
    }


    /**
     * Update
     * 유저 정보 업데이트
     * dirty-checking 방식
     * */
    public void update(String username, UserDto.Request userDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        user.update(userDto.getNickname(), userDto.getEmail(), userDto.getPhonenumber());
    }

    /**
     * Update
     * 유저의 비밀번호 변경
     * dirty-checking 방식
     * */
    public void updatePassword(String username, String checkingPassword, String changePassword) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        if (!user.isMatchPassword(passwordEncoder, checkingPassword)) {
            throw new UserException(UserExceptionType.WRONG_PASSWORD);
        }

        user.updatePassword(passwordEncoder, changePassword);
    }

    /**
     * Delete
     * 회원 탈퇴
     * 탈퇴 시 비밀번호 필요!
     * */
    public void quit(String username, String checkingPassword) {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        if (!user.isMatchPassword(passwordEncoder, checkingPassword)) {
            throw new UserException(UserExceptionType.WRONG_PASSWORD);
        }

        userRepository.delete(user);
    }

}
