# Board API Project

## 시작하며
### 1. 프로젝트 소개
 공부하면서 배운 지식들을 활용하여 웹 프로그래밍의 기본인 커뮤니티 게시판을 구현해보는 것이 목표입니다. (REST API)
 

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
<details>
<summary> 간단하게 남기는 프로젝트 구현 일지(?)</summary>
 
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

2022-12-24 토
- PostService에 게시글 저장 및 조회 기능 구현, 기본적인 CRUD 기능 모두 구현할 예정
- 조회는 게시글의 ID로 조회하는 기능과 전체 게시글 리스트 조회 기능을 구현

:christmas_tree: 2022-12-25 일 :christmas_tree:
- PostService 에 게시글 업데이트(수정), 게시글 삭제 기능 구현
- 게시글 업데이트(수정)은 Dirty Checking 방식을 통해 구현하였다. Dirty Checking 방식은 원하는 속성만 업데이트가 가능하고, 병합 방식은 모든 속성을 변경하기 때문에 Null을 업데이트 할 위험이 있다 판단하였다.
- PostSerivceTest 에 구현한 CRUD 기능들을 테스트 완료

2022-12-26 월
- PostService에 CRUD 메서드를 구현할 때도 느꼈었지만 CommentService CRUD 메서드를 구현하면서 더더욱 DTO를 Request DTO, Response DTO 분리가 필요하다고 느꼈다. 요청과 응답에 필요한 속성들이 달랐고 이를 하나의 DTO로 처리하기에는 서로 필요없는 속성들이 생겨났다. 그래서 DTO 클래스 안에 Static 클래스로 Request와 Response로 분리하였다. 이를 분리함에 따라 기존에 테스트했던 코드들을 수정해서 다시 테스트해야 하는 일이 생겼다. 다음부터 DTO를 설계할때 하나의 DTO로 모두 처리하기보단 응답과 반응의 경우를 생각해서 설계할 필요성을 느끼게 되었다.
- 추후 모든 TEST 다시 작성해서 시험해야한다....

2022-12-27 화
- Test 도중 양방향 연관관계 편의 메서드에서 계속 nullpointerexception 발생했다. List<>를 new ArrayList<>()로 초기화 시켜줘도 계속 nullpointerexception이 발생했다. 몇시간 삽질 끝에 Entity에 @Builder 가 초기화 속성을 모두 무시한다는 사실을 알아냈다...... 그래서 Entity 전체의 @Builder 속성을 제거하고 Entity 안에 @Builder 를 사용한 생성자 메서드를 따로 만들어줘서 nullpointerexception 문제를 해결하였다.
- 분리된 DTO로 기존 PostService, UserService를 다시 테스트하여 기능을 검증했다.
- 각 Entitity에 연관관계 편의 메서드를 모두 추가했다.
- CommentService CRUD 메서드 기능 테스트 완료!
 </details>
