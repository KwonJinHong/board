# Board API Project

## 시작하며
### 1. 프로젝트 소개
 공부하면서 배운 지식들을 활용하여 웹 프로그래밍의 기본인 커뮤니티 게시판을 구현해보는 것이 목표입니다. (REST API)
 

### 2. 사용하는 기술
#### 2-1. 백앤드
주요 프레임워크 및 라이브러리
- Java 11
- SpringBoot 2.7.7
- JPA (Spring Data JPA)
- Spring Security & JWT
- QueryDSL

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
![db설게_v3](https://user-images.githubusercontent.com/108498668/210064564-4458fd7a-01ec-4366-b294-346625ad3d40.png)


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
 1. JsonUsernamePasswordAuthenticationFilter : UsernamePasswordAuthenticationFilter 처럼 AbstractAuthenticationProcessingFilter를 상속받는 클래스로 attemptAuthentication() 메서드에서 JSON 데이터를 받아와  username과 password로 UsernamePasswordAuthenticationToken 만들어 getAuthenticationManager()의 authenticate 메서드 실행으로 반환
 2. LoginService : DB에서 username에 해당하는 값을 찾아와 반환 (비밀번호에 대한 검증은 DaoAuthenticationProvider에서 해준다. 따라서 DB에서 해당 User 정보를 찾아 반환만 해주면 된다.)
 3. LoginSuccessJWTProvideHandler : SimpleUrlAuthenticationSuccessHandler를 상속받아 구현, 추후에 JWT 발급하는 로직 추가 예정
 4. LoginFailureHandler : SimpleUrlAuthenticationFailureHandler를 상속받아 구현, 로그인 실패 여부를 판단하기 위해 작성

2022-12-30 금
- JWT 오픈소스 라이브러리 사용 : [auth0/java-jwt](https://github.com/auth0/java-jwt)
- JWT 관련 서비스 JwtService 구현 및 테스트 하였다. (application-jwt.yml 파일에 시크릿, Access Token 과 Refresh Token의 유효 시간을 설정해주었다.) 주요 메서드는 아래와 같다.
 1. Access Token 생성 메서드 - 암호화 알고리즘은 HMAC256 방식을 택했다. 페이로드에는 유저 ID와 Password를 담았다. 유효 기간은 
 2. Refresh Token 생성 메서드 - Refresh Token은 이 프로젝트에서 DB에 저장하고 관리하려고 한다. users 테이블에 refresh token 속성을 추가하였다. Access Token을 재발급 받기 위한 용도로만 사용할 예정이라 따로 Access Token 처럼 유저의 다른 정보를 담지 않을 것이다.
 3. Refresh Token 업데이트, 제거 메서드
 4. Access Token 및 Refresh Token 전달 메서드
 5. Access Token 및 Refresh Token 헤더 설정 메서드
 6. Access Token 추출 메서드
 7. Access Token 에서 원하는 클레임 추출 메서드 (username or password)
 8. 토큰의 유효성 검사 메서드
- Refresh Token을 DB에 저장하기 위해 User 엔티티 클래스에 refreshToken 속성 추가 및 Refresh Token 업데이트 및 제거 로직 추가
 
 2022-12-31 토
 - 로그인 성공 시 JWT를 발급하는 코드 작성
 - 로그인 성공 시에 LoginSuccessJWTProvideHandler의 onAuthenticationSuccess() 메서드에서 JWT 발급을 처리한다.
 - Access Token과 Refresh Token을 발급하고, Refresh Token은 발급한 이후 회원에게 저장해준다.
 - LoginSuccessJWTProvideHandlerTest에서 로그인 성공 시에 JWT 발급을 확인하는 테스트도 완료했다.
 
 2023-1-1 일 Happy New Year!:congratulations:
 - JWT을 통한 인증을 위한 JwtAuthenticationProcessingFilter 구현
 - "/login"을 제외한 들어오는 모든 요청에 대해서 작동하도록 필터 구현
 - 간략히 적기에는 내용들이 많아서 조만간 로그인 & JWT 통한 인증에 대한 개념 및 내용을 md 파일로 정리하여 올릴 예정
 - Access Token 및 Refresh Token 의 존재 유무, 유효 유무(?)에 대한 테스트 케이스 정리해서 테스트 
 
 2023-1-2 월
 - JWT의 payload 부분에 담기는 유저 정보 중 password 항목을 제거했다. JWT는 누구나 까서 볼 수 있다는 점을 간과했다. 중요한 정보는 담지 말아야하는데 유저의 비밀번호를 집어넣은 것은 나의 큰 불찰이었다. 미리 이렇게 수정할 수 있어서 다행이라는 생각이들었다.
 - 로그인을 구현했던 내용을 정리하여 만들었다. [로그인_구현기](https://github.com/KwonJinHong/Springboot/blob/master/Board%EA%B5%AC%ED%98%84%EA%B8%B0/%EB%A1%9C%EA%B7%B8%EC%9D%B8_%EA%B5%AC%ED%98%84.md)
 
 2023-1-3 화
 - 로그인 기능 구현 후 기존의 UserService의 회원가입 메서드를 수정하였다.
 - UserService에 회원가입, 탈퇴, 회정정보 수정(닉네임, 이메일, 전화번호), 비밀번호 변경, 회원 정보 조회, 내정보조회 메서드를 구현하였다.
 - DTO를 기존에 Request, Response로 나눴었는데, 좀 더 세분화해서 써야 할 필요성을 느꼈다. 각 메서드 별로 필요한 속성들이 다르기 때문에 메서드 별 DTO를 만드는 걸 생각중이다.
 - UserServiceTest에 각 메서드 별 기능 테스트 완료
 - [JWT 관련 내용정리](https://github.com/KwonJinHong/Springboot/blob/master/Board%EA%B5%AC%ED%98%84%EA%B8%B0/JWT_%EA%B4%80%EB%A0%A8_%EA%B5%AC%ED%98%84%EA%B8%B0.md)
 - UserDto가 기존의 Request와 Response로 구분되어 있긴 했지만, 기존 UserDTO를 삭제하고 좀 더 메서드 용도에 맞는 DTO들로 세분화 (UserJoinDto, UserInfoDto, UserUpdateDto)
 
 2023-1-4 수
 - UserApiController을 구현하였다. UserService에서 구현했던 메서드들을 각 API 요청이 오면 실행된다.
 - @DeleteMapping 에 @RequestBody JSON 파싱 오류가 나서 한참을 삽질했다. 결론은 Delete 요청은 HTTP 자체적으로 Body가 없는것을 권장한다. 따라서 @RequestBody를 통해 DTO를 전달해서 처리를 하고 싶었으나, 일단은 방법을 찾지 못해서 @PathVariable로 일단 id를 받아와 해당 Id를 갖는 유저를 탈퇴시키는 걸로 일단 기능을 변경하였다. 원래대로라면 비밀번호를 입력받아 확인 후에 탈퇴 처리를 하는 과정으로 만들고 싶었으나 이는 추후에 좀 더 알아보고 구현하는 걸로 해야겠다.
- UserApiControllerTest를 통해 각 API 별로 동작을 검증하였다.

2023-1-5 목
- PostMan으로 UserApiController의 동작을 확인하던 중, 이메일에 숫자가 들어가면 오류가 나던 문제와 로그인 성공 시에도 Refresh Token이 DB에 저장되지 않았던 문제가 발생하여 두 문제를 수정했다. 첫번째 문제는 이메일 형식의 자바 정규식에 숫자를 입력으로 포함시켜 해결했고, 두번째 문제는 로그인 성공 했을때 Handler에서 Refresh Token 발급을 JwtService를 통해 저장하는 방식으로 변경하여 해결했다. 
- PostDto를 이전의 UserDto를 메서드 용도에 맞게 세분화 시켰던 것처럼 조회, 페이징, 저장, 수정 용도에 맞게 세분화 시켰다. 기존의 만들었던 PostService의 메서드들을 좀 더 가다등멌다. JWT를 통한 인증과 로그인 기능이 추가되었기 때문에 글을 수정하거나 삭제할 때 권한을 검사하는 로직을 추가하였다. 추후에는 Comment 쪽 서비스도 가다듬어 댓글과 대댓글을 추가하여 게시글 조회 기능, 게시글 페이징 기능을 추가할 예정이다.
- 바꾼 로직으로 테스트하여 기능을 검증하였다.

2023-1-6 금
- CommentDto를 이전처럼 서비스 로직의 용도에 맞게 세분화 하였다. 
- CommentService의 기존 로직을 권한을 검증하여 댓글을 수정, 삭제 할 수 있게끔 변경하였고, 댓글 조회 기능을 사용할 일이 없을 것 같아 일단은 빼두었다.
- 댓글과 대댓글 기능을 구현하였는데 이는 설명할 내용이 좀 길어 따로 정리하여 남기겠다. -> [댓글과_대댓글_구현내용_정리](https://github.com/KwonJinHong/Springboot/blob/master/Board%EA%B5%AC%ED%98%84%EA%B8%B0/%EB%8C%93%EA%B8%80%EA%B3%BC_%EB%8C%80%EB%8C%93%EA%B8%80.md)
- CommentService의 바뀐 로직을 테스트하여 검증 완료했다.
- User Entity의 전화번호 속성의 이름을 'phonenumber'에서 'phoneNumber'로 변경하였다.

2023-1-8 일
- QueryDSL을 프로젝트에 적용하였다.
- PostService의 게시글 조회 로직을 수정하였다. 똑같이 Post id를 갖고 조회를 하지만, 이전과 달리 게시글만 조회하는게 아닌 해당 게시글을 쓴 유저의 정보도 같이 가져오게 된다. (fetch join -> @EntityGraph 사용)
- 게시글 조회 시, 해당 게시글에 달린 댓글과 대댓글도 같이 가져온다. (댓글과 대댓글을 분리하는 그룹핑하는 로직 작성 -> PostInfoDto)
- 게시글 조회 테스트 완료 (게시글 조회시 댓글 대댓글 별로 그룹핑해서 잘 가져오는지)

2023-1-9 월
- PostService에 제목이나 내용으로 게시글을 검색하는 메서드를 추가했다. (QueryDSL - 동적쿼리 사용)
- 위 메서드를 구현하기 위해 몇가지 DTO를 추가했다. (PostPagingDto - 검색 결과 정보에 대한 DTO, SimplePostInfo - 게시글 정보 간략화한 DTO, PostSearchCondition - 검색 조건)
- QueryDSL을 어떻게 적용했는지에 대한 내용은 여기에 적기에는 내용이 길어 따로 정리해서 올릴 예정이다.
- 검색 메서드에 대한 테스트 완료 (제목 or 내용 , 제목이나 내용 둘 다 적용해서 검색 가능한지)

2023-1-10 화
- PostApiController 작성 완료 (게시글 저장, 게시글 조회, 게시글 수정, 게시글 삭제, 게시글 검색)
- 위 도메인에 대한 테스트 검증 완료
- 기존 검색 @EntityGraph를 써서 페치 조인을 간단히 구현했었는데, @Query 를 써서 JPQL로 페치 조인을 구현해봄
- 기존에 잘 돌아가던 UserApiController의 기능들이 NullPointerException이 발생해서 @NoArgumentConstructor 와 Optional 초기화를 Optional.empty()로 해주어 해결하였다.
 
 2023-1-11 수
 - CommentApiController 작성 완료 (댓글 저장, 대댓글 저장, 댓글 수정, 댓글 삭제)
 - 위 기능에 대한 테스트 및 검증 완료
 - 이제 프로젝트의 막바지에 다다른것 같은 느낌이 든다. 그렇지만 아직 적용해보고 싶은 것들이 남아있고, 어떤 기술을 어떻게 프로젝트에 적용했느지 정리가 필요하다고 생각한다. 일단은 Swagger를 적용하여 API 명세서를 만들어 보는 것이 다음 목표이다.
 - 친구의 프로젝트에서 프론트 쪽과 JWT 통신이 잘 안되는 현상이 발생하였다. 현직 개발자 친구의 분석에 의하면 CORS 관련 에러가 났었고, SpringBoot 환경에서 Cors관련 설정을 해줘야 한다는 조언을 들었다. Cors 관련 설정을 해주지 않으면 프론트에서 접근하지 못한다고 한다. 그래서 이왕 배운김에 프로젝트에도 간단하게 구현해보았다.

 2023-1-12 목
 - Swagger 3.0 을 적용하여 API 명세서를 만들고 있다. 기본적으로 사용자, 게시글, 댓글 별로 API를 그룹핑하였고 각 API 마다 인증이 JWT 인증이 필요한데, 이를 한번에 해결하기 위해 JWT로 인증 토큰을 넣는 Swagger Authorize 기능을 활성화하였다.
 
 - <details> <summary> Swagger 메인 화면</summary> 
 
 ![스웨거 메인](https://user-images.githubusercontent.com/108498668/211986318-c5f68c58-35dc-499a-9bb0-5bcf53abba55.PNG)
 </details>
 
 
 </details>
