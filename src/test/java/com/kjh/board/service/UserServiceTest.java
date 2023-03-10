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

        //Spring Security에 관련되어 있어서 인증 정보를 미리 주입해주는 코드
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
     * 회원가입
     *  존재하는 아이디로 회원가입시 오류
     */
    @Test
    public void 회원가입() throws Exception {
        //given
        UserJoinDto userJoinDto = makeUserDto();

        //when
        userService.join(userJoinDto);
        clear();

        //then
        com.kjh.board.domain.user.User user = userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(()->
                new UserException(UserExceptionType.NOT_FOUND_USER));

        assertThat(user.getId()).isNotNull(); // 아이디 존재 여부
        assertThat(user.getUsername()).isEqualTo(userJoinDto.getUsername()); // USER ID가 같은지
        assertThat(user.getNickname()).isEqualTo(userJoinDto.getNickname()); // NickName 같은지
        assertThat(user.getEmail()).isEqualTo(userJoinDto.getEmail()); // Email 같은지
        assertThat(user.getPhoneNumber()).isEqualTo(userJoinDto.getPhoneNumber()); // 전화번호 같은지
        assertThat(user.getRole()).isEqualTo(Role.USER); // 권한 유저가 맞는지

    }

    @Test
    public void 회원가입_실패_원인_아이디중복() throws Exception {
        //given
        UserJoinDto userJoinDto = makeUserDto();
        userService.join(userJoinDto);
        clear();

        //when, then
        assertThat(assertThrows(UserException.class, () -> userService.join(userJoinDto)).getExceptionType()).isEqualTo(UserExceptionType.ALREADY_EXIST_USERNAME);
    }

    /**
     * 회원정보수정
     * 회원가입을 하지 않은 사람이 정보수정시 오류 -> 시큐리티 필터가 알아서 막아줄거임
     * 아이디는 변경 불가능
     * 비밀번호 변경시에는, 현재 비밀번호를 입력받아서, 일치한 경우에만 바꿀 수 있음
     * 비밀번호 변경시에는 오직 비밀번호만 바꿀 수 있음
     * 비밀번호가 아닌 닉네임,이메일,전화번호 변경 시에는, 3개를 한꺼번에 바꿀 수도 있고, 한,두개만 선택해서 바꿀수도 있음

     */
    @Test
    public void 회원수정_비밀번호수정_성공() throws Exception {
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
    public void 회원정보수정_3개다() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changeNickname = "히히";
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
    public void 회원정보수정_닉네임만() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changeNickname = "히히";

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
    public void 회원정보수정_닉네임과이메일만() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when
        String changeNickname = "히히";
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
     * 회원탈퇴
     */

    @Test
    public void 회원탈퇴() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();


        //when
        userService.quit(SecurityUtil.getLoginUsername(), PASSWORD);

        //then
        assertThat(assertThrows(UserException.class, ()-> userRepository.findByUsername(userJoinDto.getUsername()).orElseThrow(() ->
                new UserException(UserExceptionType.NOT_FOUND_USER))).getExceptionType()).isEqualTo(UserExceptionType.NOT_FOUND_USER);

    }

    @Test
    public void 회원탈퇴실패_비밀번호틀림() throws Exception {
        //given
        UserJoinDto userJoinDto = setUser();

        //when, then
        assertThat(assertThrows(UserException.class, ()-> userService.quit(SecurityUtil.getLoginUsername(), PASSWORD+12)).getExceptionType()).isEqualTo(UserExceptionType.WRONG_PASSWORD);

    }


    
    @Test
    public void 회원정보조회() throws Exception {
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
    public void 내정보조회() throws Exception {
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