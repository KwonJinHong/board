package com.kjh.board.service;

import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.dto.UserInfoDto;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.dto.UserQuitDto;
import com.kjh.board.domain.user.dto.UserUpdateDto;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.exception.UserExceptionType;
import com.kjh.board.domain.user.service.UserService;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.global.util.security.SecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired
    UserService userService;
    @PersistenceContext
    EntityManager em;
    @Autowired
    PasswordEncoder passwordEncoder;

    String PASSWORD = "1q2w3e4r!!";

    private void clear(){
        em.flush();
        em.clear();
    }

    private UserJoinDto makeUserDto() {
        return new UserJoinDto("kjh",PASSWORD,"zmfmfm","zmfmfm@dlgl.com", "010-9999-5555");
    }

    private UserJoinDto setUser() throws Exception {
        UserJoinDto userJoinDto = makeUserDto();
        userService.join(userJoinDto);
        clear();
        SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

        //Spring Security??? ???????????? ????????? ?????? ????????? ?????? ??????????????? ??????
        emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
                .username(userJoinDto.getUsername())
                .password(userJoinDto.getPassword())
                .roles(Role.USER.name())
                .build(),
                null, null));

        SecurityContextHolder.setContext(emptyContext);
        return userJoinDto;
    }

    @AfterEach
    public void removeMember(){
        SecurityContextHolder.createEmptyContext().setAuthentication(null);
    }

    /**
     * ????????????
     *  ???????????? ???????????? ??????????????? ??????
     */
    @Test
    public void ????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = makeUserDto();

        //when
        userService.join(userJoinDto);
        clear();

        //then
        com.kjh.board.domain.user.User user = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        assertThat(user.getId()).isNotNull(); // ????????? ?????? ??????
        assertThat(user.getUsername()).isEqualTo(userJoinDto.getUsername()); // USER ID??? ?????????
        assertThat(user.getNickname()).isEqualTo(userJoinDto.getNickname()); // NickName ?????????
        assertThat(user.getEmail()).isEqualTo(userJoinDto.getEmail()); // Email ?????????
        assertThat(user.getPhoneNumber()).isEqualTo(userJoinDto.getPhoneNumber()); // ???????????? ?????????
        assertThat(user.getRole()).isEqualTo(Role.USER); // ?????? ????????? ?????????

    }

    @Test
    public void ????????????_??????_??????_???????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = makeUserDto();
        userService.join(userJoinDto);
        clear();

        //when, then
        assertThat(assertThrows(UserException.class, () -> userService.join(userJoinDto)).getExceptionType()).isEqualTo(UserExceptionType.ALREADY_EXIST_USERNAME);
    }

    /**
     * ??????????????????
     * ??????????????? ?????? ?????? ????????? ??????????????? ?????? -> ???????????? ????????? ????????? ???????????????
     * ???????????? ?????? ?????????
     * ???????????? ???????????????, ?????? ??????????????? ???????????????, ????????? ???????????? ?????? ??? ??????
     * ???????????? ??????????????? ?????? ??????????????? ?????? ??? ??????
     * ??????????????? ?????? ?????????,?????????,???????????? ?????? ?????????, 3?????? ???????????? ?????? ?????? ??????, ???,????????? ???????????? ???????????? ??????

     */
    @Test
    public void ????????????_??????????????????_??????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changePassword = "##456123!!";
        userService.updatePassword(SecurityUtil.getLoginUsername(),PASSWORD, changePassword);
        clear();

        //then
        com.kjh.board.domain.user.User findUser = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        assertThat(findUser.isMatchPassword(passwordEncoder, changePassword)).isTrue();
    }

    @Test
    public void ??????????????????_3??????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changeNickname = "??????";
        String changeEmail = "change@change";
        String chanegPhoneNumber = "01099999999";

        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickname), Optional.of(changeEmail), Optional.of(chanegPhoneNumber));

        userService.update(userJoinDto.getUsername(), userUpdateDto);
        clear();

        //then
        com.kjh.board.domain.user.User user = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        assertThat(user.getNickname()).isEqualTo(changeNickname);
        assertThat(user.getEmail()).isEqualTo(changeEmail);
        assertThat(user.getPhoneNumber()).isEqualTo(chanegPhoneNumber);
    }

    @Test
    public void ??????????????????_????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changeNickname = "??????";

        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickname), Optional.empty(), Optional.empty());

        userService.update(userJoinDto.getUsername(), userUpdateDto);
        clear();

        //then
        com.kjh.board.domain.user.User user = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        assertThat(user.getNickname()).isEqualTo(changeNickname);
        assertThat(user.getEmail()).isEqualTo(userJoinDto.getEmail());
        assertThat(user.getPhoneNumber()).isEqualTo(userJoinDto.getPhoneNumber());

    }

    @Test
    public void ??????????????????_????????????????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changeNickname = "??????";
        String changeEmail = "change@change";

        UserUpdateDto userUpdateDto = new UserUpdateDto(Optional.of(changeNickname), Optional.of(changeEmail), Optional.empty());

        userService.update(userJoinDto.getUsername(), userUpdateDto);
        clear();

        //then
        com.kjh.board.domain.user.User user = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        assertThat(user.getNickname()).isEqualTo(changeNickname);
        assertThat(user.getEmail()).isEqualTo(changeEmail);
        assertThat(user.getPhoneNumber()).isEqualTo(userJoinDto.getPhoneNumber());
    }


    /**
     * ????????????
     */

    @Test
    public void ????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();


        //when
        userService.quit(SecurityUtil.getLoginUsername(), PASSWORD);

        //then
        assertThat(assertThrows(UserException.class, ()-> userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(() ->
                new UserException(UserExceptionType.NOT_FOUND_USER))).getExceptionType()).isEqualTo(UserExceptionType.NOT_FOUND_USER);

    }

    @Test
    public void ??????????????????_??????????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when, then
        assertThat(assertThrows(UserException.class, ()-> userService.quit(SecurityUtil.getLoginUsername(), PASSWORD+12)).getExceptionType()).isEqualTo(UserExceptionType.WRONG_PASSWORD);

    }


    
    @Test
    public void ??????????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();
        com.kjh.board.domain.user.User user = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));
        clear();

        //when
        UserInfoDto userInfo = userService.getInfo(user.getId());
        
        //then
        assertThat(userInfo.getUsername()).isEqualTo(userJoinDto.getUsername());
        assertThat(userInfo.getNickname()).isEqualTo(userJoinDto.getNickname());
        assertThat(userInfo.getEmail()).isEqualTo(userJoinDto.getEmail());
        assertThat(userInfo.getPhoneNumber()).isEqualTo(userJoinDto.getPhoneNumber());

    }

    @Test
    public void ???????????????() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        UserInfoDto myInfo = userService.getMyInfo();

        //then
        assertThat(myInfo.getUsername()).isEqualTo(userJoinDto.getUsername());
        assertThat(myInfo.getNickname()).isEqualTo(userJoinDto.getNickname());
        assertThat(myInfo.getEmail()).isEqualTo(userJoinDto.getEmail());
        assertThat(myInfo.getPhoneNumber()).isEqualTo(userJoinDto.getPhoneNumber());


    }


}