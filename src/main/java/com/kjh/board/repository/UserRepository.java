package com.kjh.board.repository;

import com.kjh.board.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 닉네임으로 User 정보 가져오기
     * */
    User findByNickname(String nickname);
}
