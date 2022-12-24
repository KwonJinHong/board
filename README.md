# Board Project

## 시작하며
### 1. 프로젝트 소개
 공부하면서 배운 지식들을 활용하여 웹 프로그래밍의 기본인 커뮤니티 게시판을 구현해보는 것이 1차 목표입니다. 
<br> 그리고 이 게시판 프로젝트를 토대로 쇼핑몰 + 게시판이 합쳐진 중고거래사이트를 구현하는 것이 2차 목표입니다.
<br> 조금 정리가 안된 느낌이지만 최대한 배운것들을 활용해서 제로베이스에서 배포까지 해보는 것을 최종 목표로 하고 있습니다.

### 2. 사용하는 기술
#### 2-1. 백앤드
주요 프레임워크 및 라이브러리
- Java 11
- SpringBoot 2.7.6
- JPA (Spring Data JPA)

Build Tool
- Gradle 7.6

DataBase
- H2 Database 2.1.214

#### 2-2. 프론트앤드
- HTML/CSS
- Thymeleaf

## 구조 및 설계
### 1. 패키지 구성

### 2. DB 설계
![db설계](https://user-images.githubusercontent.com/108498668/208923244-9e2e14be-452e-4e69-ac50-0981bdb4bda0.png)

## History 
 간단하게 남기는 프로젝트 구현 일지(?)
 
2022-12-21 수
- 사용자, 게시글, 댓글을 DB 테이블로 설계했다.
- 초기 구상이며 추가해야하는 부분이 있다고 생각한다.
- 추후에 로그인 기능이 추가되면 users 테이블에 패스워드 관련 컬럼이 생성되어야한다.

2022-12-22 목
- Spring Data JPA를 활용하여 Repository 패키지 생성 및 각 엔티티 리포지토리 구현

2022-12-23 금
- Service, Controller, Dto 패키지를 생성
- UserService에 간단한 회원가입 메서드 구현
- UserApiController 구현 (회원 가입, 회원 목록 조회)