package com.kjh.board.domain.post.repository;

import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.post.condition.PostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.kjh.board.domain.post.QPost.post;
import static com.kjh.board.domain.user.QUser.user;

@Repository
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory query;

    public PostQueryRepositoryImpl(JPAQueryFactory query) {
        this.query = query;
    }

    @Override
    public Page<Post> search(PostSearchCondition postSearchCondition, Pageable pageable) {

        List<Post> content = query.selectFrom(post)
                                    .where(
                                            contentContain(postSearchCondition.getContent()),
                                            titleContain(postSearchCondition.getTitle())
                                    )
                .leftJoin(post.user, user)
                .fetchJoin()
                            .orderBy(post.createdDate.desc())
                            .offset(pageable.getOffset())
                            .limit(pageable.getPageSize())
                            .fetch();


        JPAQuery<Post> countQuery = query.selectFrom(post)
                                            .where(
                                                    contentContain(postSearchCondition.getContent()),
                                                    titleContain(postSearchCondition.getTitle())
                                            );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetch().size());
    }

    private BooleanExpression titleContain(String title) {
        /**
         * 주어진 문자열이 null 이거나 길이가 0 인지 확인한다.
         * null 이거나 0 이면, 결과를 null로 반환한다.
         * null 이거나 0 이 아니라면 해당 문자열을 제목에 포함하는 게시글을 찾는다.
         * */
        return StringUtils.hasLength(title) ? post.title.contains(title) : null;
    }

    private BooleanExpression contentContain(String content) {
        /**
         * 주어진 문자열이 null 이거나 길이가 0 인지 확인한다.
         * null 이거나 0 이면, 결과를 null로 반환한다.
         * null 이거나 0 이 아니라면 해당 문자열을 내용에 포함하는 게시글을 찾는다.
         * */
        return StringUtils.hasLength(content) ? post.content.contains(content) : null;
    }

}
