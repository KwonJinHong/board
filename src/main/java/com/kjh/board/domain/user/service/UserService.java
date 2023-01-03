package com.kjh.board.domain.user.service;

import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.dto.UserInfoDto;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.dto.UserUpdateDto;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.exception.UserExceptionType;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.global.util.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void join(UserJoinDto userJoinDto) {

        User user = userJoinDto.toEntity();
        user.addUserAuthority();
        user.encodePassword(passwordEncoder);

        if(userRepository.findByUsername(userJoinDto.getUsername()).isPresent()) {
            throw new UserException(UserExceptionType.ALREADY_EXIST_USERNAME);
        }

        userRepository.save(user);
    }

    /**
     * Read
     * 유저 정보 가져오기
     * */
    public UserInfoDto getInfo(Long id) {
        User findUser = userRepository.findById(id).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));
        //Entity -> DTO로 변환
        return new UserInfoDto(findUser);
    }

    /**
     * Read
     * 내 정보 가져오기
     * */
    public UserInfoDto getMyInfo() {
        User findUser = userRepository.findByUsername(SecurityUtil.getLoginUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));
        //Entity -> DTO로 변환
        return new UserInfoDto(findUser);
    }


    /**
     * Update
     * 유저 정보 업데이트
     * dirty-checking 방식
     * */
    public void update(String username, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByUsername(username).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        userUpdateDto.getNickname().ifPresent(user::updateNickname);
        userUpdateDto.getEmail().ifPresent(user::updateEmail);
        userUpdateDto.getPhonenumber().ifPresent(user::updatePhoneNumber);
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
