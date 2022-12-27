package com.kjh.board.domain.user.repository;

import com.kjh.board.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 닉네임으로 User 정보 가져오기
     * */
    User findByNickname(String nickname);
}
