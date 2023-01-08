package com.kjh.board.domain.post.repository;

import com.kjh.board.domain.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * @EntityGraph는 페치 조인을 간단하게 사용할 수 있게 해주는 어노테이션
     * "select p from Post p join fetch p.user pu where p.id = :id"
     */
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findWithUserById(Long id);
}
