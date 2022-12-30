package com.kjh.board.domain.user.repository;

import com.kjh.board.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 닉네임으로 User 정보 가져오기
     * */
    User findByNickname(String nickname);

    Optional<User> findByUsername(String username);

    /**
     * Refresh Token을 통해 유저 정보 조회
     * */
    Optional<User> findByRefreshToken(String refreshToken);
}
