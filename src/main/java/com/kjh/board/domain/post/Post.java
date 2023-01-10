package com.kjh.board.domain.post;

import com.kjh.board.domain.BaseTimeEntity;
import com.kjh.board.domain.user.User;
import com.kjh.board.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 350, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    //==게시글 삭제시, 달려있는 댓글 모두 같이 삭제 처리==//
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    //==연관관계 편의 메서드==//
    public void confirmWriter(User user) {
        //writer는 변경이 불가능하므로 이렇게만 해주어도 될듯
        this.user = user;
        user.addPost(this);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;

    }

    //==게시글 업데이트==//
    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }


}
