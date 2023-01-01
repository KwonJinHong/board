package com.kjh.board.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.domain.user.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public class JwtService {

    @Value("${jwt.secret}")
    private String secret; // 서버가 갖고 있는 시크릿

    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds; // Access Token 유효 시간

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenValidityInSeconds; // Refresh Token 유효 시간

    @Value("${jwt.access.header}")
    private String accessHeader; // Access Token 헤더

    @Value("${jwt.refresh.header}")
    private String refreshHeader; // Refresh Token 헤더

    //자주 쓰이는 문자열 변수로 선언언
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "username";
    private static final String PASSWORD_CLAIM = "password";
    private static final String BEARER = "Bearer ";

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * Access Token 생성 메서드
     * username과 password를 비공개 클레임으로 사용
     * */
    public String createAccessToken(String username, String password) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
                .withClaim(USERNAME_CLAIM, username)
                .withClaim(PASSWORD_CLAIM, password)
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * Refresh Token 생성 메서드
     * Access Token 재발급하는 용도로 사용할 것이기 때문에 다른 정보를 넣지 않았다.
     * DB의 users 테이블에 저장하여 관리
     * */
    public String createRefreshToken() {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds * 1000))
                .sign(Algorithm.HMAC512(secret));
    }

    /**
     * Refresh Token 업데이트 메서드
     * */
    public void updateRefreshToken(String username, String refreshToken) {
        userRepository.findByUsername(username)
                .ifPresentOrElse(
                        user -> user.updateRefreshToken(refreshToken),
                        () -> new Exception("유저가 없습니다")
                );
    }

    /**
     * Refresh Token 제거 메서드
     * */
    public void removeRefreshToken(String username) {
        userRepository.findByUsername(username)
                .ifPresentOrElse(
                        user -> user.removeRefreshToken(),
                        () -> new Exception("유저가 없습니다")
                );
    }

    /**
     * Access Token과 Refresh Token을 response 헤더에 넣어준다.
     * Access Token과 Refresh Token을 둘다 필요할 때 사용
     * */
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken){
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
        tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);

    }

    /**
     * Access Token을 response 헤더에 넣는다.
     * Access Token 만 필요할 때 사용
     * */
    public void sendAccessToken(HttpServletResponse response, String accessToken){
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
    }

    /**
     * Access Token을 request 헤더에서 추출
     * */
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader)).filter(

                accessToken -> accessToken.startsWith(BEARER)

        ).map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /**
     * Refresh Token을 request 헤더에서 추출
     * */
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader)).filter(

                refreshToken -> refreshToken.startsWith(BEARER)

        ).map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /**
     * Access Token으로부터 username을 추출
     * */
    public Optional<String> extractUsername(String accessToken) {
        try {
                return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret)) // 토큰의 서명의 유효성을 검사하는데 사용할 알고리즘이 있는 JWT verifier builder를 반환
                        .build() // 반환된 빌더로 JWT verifier 생성
                        .verify(accessToken) // Access Token을 검증하고, 유효하지 않으면 예외를 발생시킴
                        .getClaim(USERNAME_CLAIM) // 해당 클레임을 가져옴
                        .asString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Access Token으로부터 password를 추출
     * */
    public Optional<String> extractPassword(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secret))
                    .build()
                    .verify(accessToken)
                    .getClaim(PASSWORD_CLAIM)
                    .asString());
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    /**
     * 응답(reponse) 헤더에 Access Token 넣어줌
     * */
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    /**
     * 응답(reponse) 헤더에 Refresh Token 넣어줌
     * */
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }


    /**
     * 토큰의 유효성 검사
     * */
    public boolean isTokenValid(String token){
        try {
            JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
            return true;
        }catch (Exception e){
            new Exception("유효한 토큰이 아닙니다!!");
            return false;
        }
    }

}
