package com.kjh.board.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjh.board.domain.user.repository.UserRepository;
import com.kjh.board.domain.user.service.LoginService;
import com.kjh.board.global.jwt.service.JwtService;
import com.kjh.board.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.kjh.board.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.kjh.board.global.login.handler.LoginFailureHandler;
import com.kjh.board.global.login.handler.LoginSuccessJWTProvideHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final LoginService loginService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final CorsConfig corsConfig;

    private static final String[] PERMIT_URL_ARRAY = {
            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
                    .formLogin().disable()// formLogin ???????????? ????????????
                    .httpBasic().disable()//httpBasic ???????????? ????????????(username??? password??? ?????? ???????????? ????????? ??????)
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)


                    .and()
                    .authorizeRequests()
                    .antMatchers("/login", "/join","/").permitAll() // ?????????, ????????????, ???????????????, Swagger-ui??? ?????? ????????? ?????? ??????
                    .antMatchers(PERMIT_URL_ARRAY).permitAll()
                    .anyRequest().authenticated();

         http.addFilter(corsConfig.corsFilter());

         /**
          * ?????????????????? ???????????? ????????? ????????? ???????????????
          * ?????? FormLogin ??????????????? UsernamePasswordAuthenticationFilter?????? username??? password??? ?????? userpasswordAuthenticationToken ??????
          * ????????? JSON?????? ???????????? ????????? ??? userpasswordAuthenticationToken??? ???????????? ?????? JsonUsernamePasswordLoginFilter??? ?????????
          * ????????? ???????????? ??????????????? LogoutFilter ?????? UsernamePasswordAuthenticationFilter??? ?????????
          * ????????? JsonUsernamePasswordLoginFilter??? LogoutFilter ?????? ?????? ??????????????? ??????
          * */
        http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);
        http.addFilterBefore(jwtAuthenticationProcessingFilter(), JsonUsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {//2 - AuthenticationManager ??????
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());//PasswordEncoder?????? PasswordEncoderFactories.createDelegatingPasswordEncoder() ??????
        provider.setUserDetailsService(loginService);
        return new ProviderManager(provider);
    }

    @Bean
    public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler(){
        return new LoginSuccessJWTProvideHandler(jwtService);
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

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter(){
        JwtAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter = new JwtAuthenticationProcessingFilter(userRepository, jwtService);

        return jsonUsernamePasswordLoginFilter;
    }
}
