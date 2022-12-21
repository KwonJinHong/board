package com.kjh.board.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id @GeneratedValue
    private Long id;

    @Column(length = 15, nullable = false, unique = true)
    private String username; // 사용자 ID, 추후에 password도 추가해서 로그인 기능 구현 예정

    @Column(length = 30, nullable = false, unique = true)
    private String nickname; // 사용자가 정하는 닉네임

    @Column(length = 11, nullable = false, unique = true)
    private String phonenumber; // 사용자 이메일

    @Column(length = 50, nullable = false, unique = true)
    private String email; // 사용자 이메일


}
