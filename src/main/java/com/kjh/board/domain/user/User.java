package com.kjh.board.domain.user;

import com.kjh.board.domain.BaseTimeEntity;
import com.kjh.board.domain.comment.Comment;
import com.kjh.board.domain.post.Post;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    private String username; // 사용자 ID, 추후에 password도 추가해서 로그인 기능 구현 예정

    private String password;//비밀번호

    @Column(length = 30, nullable = false, unique = true)
    private String nickname; // 사용자가 정하는 닉네임

    @Column(length = 11, nullable = false, unique = true)
    private String phonenumber; // 사용자 이메일

    @Column(length = 50, nullable = false, unique = true)
    private String email; // 사용자 이메일

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1000)
    private String refreshToken;//RefreshToken

    //== 패스워드 암호화 ==//
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }

    //==유저 정보 수정==//
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void removeRefreshToken() {
        this.refreshToken = null;
    }





    //== 회원탈퇴 -> 작성한 게시물, 댓글 모두 삭제 ==//
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    //== 연관관계 메서드 ==//
    public void addPost(Post post){
        //post의 user 설정은 post에서 함
        posts.add(post);
    }

    public void addComment(Comment comment){
        //comment의 user 설정은 comment에서 함
        comments.add(comment);
    }


}
