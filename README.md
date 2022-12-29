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
- Spring Security & JWT

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
![db설계_v2](https://user-images.githubusercontent.com/108498668/209804836-9b118871-00db-40e3-9cc2-79b8df18d738.png)

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
- 패키지 구조를 변경하였다. 기존에는 domain, service, repostiory, controller, dto 등 각 패키지에 user, post, comment에 관련된 클래스들이 섞여있었다. 조금씩 클래스들이 늘어나고 난잡해지는 느낌이 들었다. 좀 더 깔끔하게 정리하여 아예 도메인 별로 관련된 클래스들을 나눠놓았다. (예를 들면, user 패키지에는 user에 관련된 dto, service, controller, exception 등만 존재하게끔) 이렇게 바꿔놓으니 직관성이 좀더 좋아진듯하다.
- Exception 클래스들을 사용자 정의 클래스로 만들어놓았다. 기존에는 illegalargumentexception 하나만 사용했는데 이렇게 하니까 일일히 에러메세지를 정해줘야하고, 어느곳과 관련된 예외인지 직관성이 떨어진다고 느꼈다. 그래서 각 도메인 별로 사용자 정의 예외 클래스들을 구현해 각 서비스 메서드에 적용시켜 놓았다. HTTP STATUS 코드도 지정할 수 있어서 코드를 통해서도 어느곳과 관련된 예외인지 금방 알아차릴수 있다. (User - 6XX, Post - 7XX, Comment - 8XX 로 설정해놓았다.)

2022-12-28 수
- Spring Security를 적용해 JWT까지 구현해 로그인 서비스를 구현해보고자 한다. 기존 User 엔티티에 password 속성을 추가해주었다.
- Spring Security를 제대로 적용하기 위해 config 패키지를 만들어 SecurityConfig 클래스를 만들었다.
- SecurityConfig에 기본적인 설정을 세팅했다. formlogin(), httpBasic(), csrf() 등 설정을 disable 시켜놨고, JWT 방식을 쓰기위해 sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)를 설정해주었다.
- passwordEncoder를 사용해 password를 암호화 하는 테스트를 완료했다. (암호화된 비밀번호와 기존 비밀번호가 서로 매치되는 것도 확인!)

2022-12-29 목
- 로그인 인증방식을 Form Login 방식이 아닌 JSON으로 데이터를 받아와 로그인하는 방식으로 구현하기 위해서 몇가지 사전작업을 하였다.
- Form Login 방식에 대해 간략한 설명을 남기자면
 1. /login에 POST 방식으로 들어오는 요청에 의해 작동
 2. username과 password를 파라미터로 가지고 있어야 한다.
 3. username과 password를 갖고 UsernamepasswordAuthenticationToken을 생성한다.
 4. 3과정에서 생성한 토큰을 AuthenticationManager의 Authenticate() 메서드 인자로 넘겨주고, 해당 메서드의 반환 결과인 Authentication 객체를 반환한다.
 
- 이 프로젝트에서는 Form Login 방식이 아닌 JSON 으로 데이터를 받아올것이기 때문에 JSON 받아와 UsernamepasswordAuthenticationToken을 생성한다. 아래는 구현한 클래스에 대한 간단한 설명이다.
 1. JsonUsernamePasswordAuthenticationFilter : AbstractAuthenticationProcessingFilter를 상속받는 클래스로 attemptAuthentication() 메서드에서 username과 password로 UsernamePasswordAuthenticationToken 만들어 getAuthenticationManager()의 authenticate 메서드 실행으로 반환
 2. LoginService : DB에서 username에 해당하는 값을 찾아와 반환 (비밀번호에 대한 검증은 DaoAuthenticationProvider에서 해준다. 따라서 DB에서 해당 User 정보를 찾아 반환만 해주면 된다.)
 3. LoginSuccessJWTProvideHandler : SimpleUrlAuthenticationSuccessHandler를 상속받아 구현, 추후에 JWT 발급하는 로직 추가 예정
 4. LoginFailureHandler : SimpleUrlAuthenticationFailureHandler를 상속받아 구현, 로그인 실패 여부를 판단하기 위해 작성

 </details>
