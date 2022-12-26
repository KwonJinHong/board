package com.kjh.board.service;

import com.kjh.board.domain.User;
import com.kjh.board.dto.UserDto;
import com.kjh.board.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserRepository userRepository;
    @Autowired UserService userService;
    @PersistenceContext
    EntityManager em;

    @Test
    public void 회원가입() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("크르르").phonenumber("01090765644").email("wlsghd328@gmail.com").build();
        em.persist(user);

        //when
        UserDto.Request userDto = new UserDto.Request();
        Long userID = userService.join(userDto);

        //then
        System.out.println(userID);

    }

    @Test
    public void 회원조회() throws Exception {
        //given
        User user = User.builder().username("kjh").nickname("크르르").phonenumber("01090765644").email("wlsghd328@gmail.com").build();
        em.persist(user);

        //when
        List<UserDto.Response> users = userService.findAll();

        //then
        System.out.println(users.get(0));


    }
}