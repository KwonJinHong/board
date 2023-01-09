package com.kjh.board.domain.post.repository;

import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.condition.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostQueryRepository {

    Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable);


}
