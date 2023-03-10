package com.kjh.board.global.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.domain.user.Role;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.logging.NDC.clear;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "kjh";
    private static String PASSWORD = "1q2w3e4r!!";

    private static String LOGIN_RUL = "/login";


    private void clear(){
        em.flush();
        em.clear();
    }

    @BeforeEach
    private void init(){
        userRepository.save(User.builder()
                .username(USERNAME)
                .password(delegatingPasswordEncoder.encode(PASSWORD))
                .nickname("Member1")
                .role(Role.USER)
                .email("zmfmfm@zmfmfmf.com")
                .phoneNumber("010-1111-2222")
                .build());
        clear();
    }



    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private ResultActions perform(String url, MediaType mediaType, Map usernamePasswordMap) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders
                .post(url) // POST ??????
                .contentType(mediaType)
                .content(objectMapper.writeValueAsString(usernamePasswordMap)));

    }

    @Test
    public void ?????????_??????() throws Exception {
        //given

        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD); // id??? password??? Map ?????????

        //when, then
        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map) // "/login" ????????? Map??? JSON ????????? ??????
                .andDo(print()) // ????????? print
                .andExpect(status().isOk()) // ?????? status??? ok??? ?????????
                .andReturn();

    }

    @Test
    public void ?????????_??????_???????????????() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME+"dndn", PASSWORD);

        //when, then
        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void ?????????_??????_??????????????????() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD+"123");


        //when, then
        MvcResult result = perform(LOGIN_RUL, APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void ?????????_?????????_?????????_FORBIDDEN() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when, then
        perform(LOGIN_RUL+"123", APPLICATION_JSON, map)
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    public void ?????????_HTTP_METHOD_GET??????_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .get(LOGIN_RUL)     // GET ??????
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



    @Test
    public void ??????_?????????_HTTP_METHOD_PUT??????_NOTFOUND() throws Exception {
        //given
        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);


        //when
        mockMvc.perform(MockMvcRequestBuilders
                        .put(LOGIN_RUL)     // PUT ??????
                        .contentType(APPLICATION_FORM_URLENCODED)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }



}
