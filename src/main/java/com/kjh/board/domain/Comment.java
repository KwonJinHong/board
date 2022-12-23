package com.kjh.board.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
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

    //==연관관계 생성 매서드==//
    private void changePost(Post post) {
        this.post = post;
        post.getComments().add(this);
    }

}
