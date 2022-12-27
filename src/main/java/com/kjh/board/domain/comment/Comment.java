package com.kjh.board.domain.comment;

import com.kjh.board.domain.BaseTimeEntity;
import com.kjh.board.domain.post.Post;
import com.kjh.board.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    //==연관관계 편의 매서드==//
    public void confirmWriter(User user) {
        this.user = user;
        user.addComment(this);
    }

    public void confirmPost(Post post) {
        this.post = post;
        post.addComment(this);
    }

    //==댓글 내용 수정(업데이트)==//

    public void update(String content) {
        this.content = content;
    }

    @Builder
    public Comment(Long id, User user, String content, Post post) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.post = post;
    }
}
