package com.kjh.board.domain.post.repository;

import com.kjh.board.domain.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> , PostQueryRepository{

    /**
     * @EntityGraph는 페치 조인을 간단하게 사용할 수 있게 해주는 어노테이션
     * "select p from Post p join fetch p.user"
     * post id로 User 테이블에 해당 post id를 갖는 유저를 같이 가져옴
     */
    //@EntityGraph(attributePaths = {"user"})
    @Query("select DISTINCT p from Post p join fetch p.user")
    Optional<Post> findWithUserById(Long id);
}
