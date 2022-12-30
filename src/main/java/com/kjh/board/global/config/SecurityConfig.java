package com.kjh.board.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.kjh.board.global.login.handler.LoginFailureHandler;
import com.kjh.board.global.login.handler.LoginSuccessJWTProvideHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    //private final LoginService loginService;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                    .formLogin().disable()// formLogin 인증방법 비활성화
                    .httpBasic().disable()//httpBasic 인증방법 비활성화(username과 password가 직접 노출되고 암호화 불가)
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                    .and()
                    .authorizeRequests()
                    .antMatchers("/login", "/signUp","/").permitAll() // 로그인, 회원가입, 메인페이지는 인증 없이도 접근 허가
                    .anyRequest().authenticated();

         /**
          * 시큐리티에서 실행되는 필터의 순서가 존재하는데
          * 기존 FormLogin 방식에서는 UsernamePasswordAuthenticationFilter에서 username과 password를 갖고 userpasswordAuthenticationToken 생성
          * 그러나 JSON으로 데이터를 받아와 위 userpasswordAuthenticationToken를 생성하기 위해 JsonUsernamePasswordLoginFilter를 구현함
          * 기존의 시큐리티 필터순서가 LogoutFilter 뒤에 UsernamePasswordAuthenticationFilter가 실행됨
          * 그래서 JsonUsernamePasswordLoginFilter도 LogoutFilter 실행 후에 실행되도록 설정
          * */
        http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {//2 - AuthenticationManager 등록
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();//DaoAuthenticationProvider 사용
        provider.setPasswordEncoder(passwordEncoder());//PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler();
    }

    @Bean
    public LoginFailureHandler loginFailureHandler(){
        return new LoginFailureHandler();
    }

    @Bean
    public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter(){
        JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessJWTProvideHandler());
        jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());

        return jsonUsernamePasswordLoginFilter;
    }
}