package com.kjh.board.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.dto.UserJoinDto;
import com.kjh.board.domain.user.exception.UserException;
import com.kjh.board.domain.user.exception.UserExceptionType;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @PersistenceContext
    EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    PasswordEncoder passwordEncoder;

    private static String JOIN_URL = "/join";

    private String username = "kjh1234"; // 6~20 사이 자릿수
    private String password = "tlqkfdho!@#13"; // 알파벳, 숫자, 특수문자 조합
    private String nickname = "zmfmfm"; // 2글자 이상
    private String email = "zmfmfm@zmfm.com"; // 이메일 형식
    private String phoneNumber = "000-1111-2222"; // -을 포함한 전화번호

    private void clear(){
        em.flush();
        em.clear();
    }

    private void join(String joinData) throws Exception {
        mockMvc.perform(
                        post(JOIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(joinData))
                .andExpect(status().isOk());
    }

    private void joinFail(String joinData) throws Exception {
        mockMvc.perform(
                        post(JOIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(joinData))
                .andExpect(status().isBadRequest());
    }

    @Value("${jwt.access.header}")
    private String accessHeader;

    private static final String BEARER = "Bearer ";

    private String getAccessToken() throws Exception {

        Map<String, String> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);


        MvcResult result = mockMvc.perform(
                        post("/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk()).andReturn();

        return result.getResponse().getHeader(accessHeader);
    }

    @Test
    public void 회원가입_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));

        //when
        join(joinData);

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 회원가입_실패_필수_입력_정보_없음() throws Exception {
        //given
        String noUsernameJoinData = objectMapper.writeValueAsString(new UserJoinDto(null, password, nickname, email, phoneNumber));
        String noPasswordJoinData = objectMapper.writeValueAsString(new UserJoinDto(username, null, nickname, email, phoneNumber));
        String noNicknameJoinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, null, email, phoneNumber));
        String noEmailJoinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, null, phoneNumber));
        String noPhonenumberJoinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, null));

        //when, then
        joinFail(noUsernameJoinData);
        joinFail(noPasswordJoinData);
        joinFail(noNicknameJoinData);
        joinFail(noEmailJoinData);
        joinFail(noPhonenumberJoinData);

        assertThat(userRepository.findAll().size()).isEqualTo(0);
    }

    @Test
    public void 회원정보수정_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));

        join(joinData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("nickname",nickname+"히히");
        map.put("email", "change@chnage.com");
        map.put("phoneNumber", "999-9999-9999");
        String updateUserData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/user")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateUserData))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getNickname()).isEqualTo(nickname+"히히");
        assertThat(user.getEmail()).isEqualTo("change@chnage.com");
        assertThat(user.getPhoneNumber()).isEqualTo("999-9999-9999");
        assertThat(userRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void 회원정보수정_닉네임만변경_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));

        join(joinData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("nickname",nickname+"히히");
        String updateUserData = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/user")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateUserData))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getNickname()).isEqualTo(nickname+"히히");
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(userRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void 회원정보수정_이메일만변경_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));

        join(joinData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("email", "change@chnage.com");
        String updateUserData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/user")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateUserData))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo("change@chnage.com");
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(userRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    public void 회원정보수정_전화번호만변경_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));

        join(joinData);

        String accessToken = getAccessToken();
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNumber", "999-9999-9999");
        String updateUserData = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/user")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateUserData))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPhoneNumber()).isEqualTo("999-9999-9999");
        assertThat(userRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    public void 비밀번호수정_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkingPassword",password);
        map.put("changePassword",password+"123!@#abc");

        String updatePassword = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/user/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isOk());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(passwordEncoder.matches(password, user.getPassword())).isFalse();
        assertThat(passwordEncoder.matches(password+"123!@#abc", user.getPassword())).isTrue();
    }

    @Test
    public void 비밀번호수정_실패_검증비번틀림() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkingPassword","avc123!@#$");
        map.put("changePassword",password+"123!@#abc");

        String updatePassword = objectMapper.writeValueAsString(map);


        //when
        mockMvc.perform(
                        put("/user/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(password+"123!@#abc", user.getPassword())).isFalse();
    }

    @Test
    public void 비밀번호수정_실패_바꾸려는_비번형식틀림() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);

        String accessToken = getAccessToken();

        Map<String, Object> map = new HashMap<>();
        map.put("checkingPassword",password);
        map.put("changePassword","123456");

        String updatePassword = objectMapper.writeValueAsString(map);

        //when
        mockMvc.perform(
                        put("/user/password")
                                .header(accessHeader,BEARER+accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatePassword))
                .andExpect(status().isBadRequest());

        //then
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(passwordEncoder.matches(password, user.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("123456", user.getPassword())).isFalse();
    }

    @Test
    public void 회원탈퇴_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);

        String accessToken = getAccessToken();

        Long id = userRepository.findAll().get(0).getId();

        //when
        mockMvc.perform(
                        delete("/user/" + id)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader,BEARER+accessToken))
                .andExpect(status().isOk());

        //then
        assertThrows(UserException.class, () -> userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER)));
    }

    @Test
    public void 내정보조회_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);


        String accessToken = getAccessToken();


        //when
        MvcResult result = mockMvc.perform(
                        get("/user")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getUsername()).isEqualTo(map.get("username"));
        assertThat(user.getNickname()).isEqualTo(map.get("nickname"));
        assertThat(user.getEmail()).isEqualTo(map.get("email"));
        assertThat(user.getPhoneNumber()).isEqualTo(map.get("phoneNumber"));

    }

    @Test
    public void 내정보조회_실패_JWT없음() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);


        String accessToken = getAccessToken();


        //when, then
        MvcResult result = mockMvc.perform(
                        get("/user")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isForbidden()).andReturn();

    }

    @Test
    public void 회원정보조회_성공() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);


        String accessToken = getAccessToken();

        Long id = userRepository.findAll().get(0).getId();

        //when

        MvcResult result = mockMvc.perform(
                        get("/user/"+id)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk()).andReturn();


        //then
        Map<String, Object> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        assertThat(user.getUsername()).isEqualTo(map.get("username"));
        assertThat(user.getNickname()).isEqualTo(map.get("nickname"));
        assertThat(user.getEmail()).isEqualTo(map.get("email"));
        assertThat(user.getPhoneNumber()).isEqualTo(map.get("phoneNumber"));
    }

    @Test
    public void 회원정보조회_실패_없는회원조회() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);


        String accessToken = getAccessToken();


        //when

        MvcResult result = mockMvc.perform(
                        get("/user/123")
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isNotFound()).andReturn();


        //then
        Map<String, Integer> map = objectMapper.readValue(result.getResponse().getContentAsString(), Map.class);
        assertThat(map.get("errorCode")).isEqualTo(UserExceptionType.NOT_FOUND_USER.getErrorCode());//빈 문자열
    }

    @Test
    public void 회원정보조회_실패_JWT없음() throws Exception {
        //given
        String joinData = objectMapper.writeValueAsString(new UserJoinDto(username, password, nickname, email, phoneNumber));
        join(joinData);

        String accessToken = getAccessToken();

        Long id = userRepository.findAll().get(0).getId();

        //when,then
        mockMvc.perform(
                        get("/user/" + id)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .header(accessHeader, BEARER + accessToken+1))
                .andExpect(status().isForbidden());

    }

}