package com.carrot.nara.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.carrot.nara.handler.CustomAuthenticationSuccessHandler;


@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable(); // 토큰이 필요한 post/put/delete 요청에 대해 403err를 막고 구현을 간단히 하기 위해
        
        http.formLogin()
            .loginPage("/user/signin")
            /*.successHandler(customAuthenticationSuccessHandler)*/
            .defaultSuccessUrl("/")
            .permitAll();
        
        http.logout()
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl("/")
            .invalidateHttpSession(true);
        
        http.authorizeHttpRequests() // 요청에 따른 권한 설정 시작.
            .requestMatchers("") //  로 시작하는 모든 경로 .hasRole("USER")로 원하는 권한에 해당하는 설정
            .hasRole("USER")
            .anyRequest() // 그 이외의 모든 요청
            .permitAll();
        
        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            web
                // 정적 자원들(static 폴더 안의 파일들)을 스프링 시큐리티 적용에서 제외시키기 위해서.
                .ignoring()
                .requestMatchers(
                    PathRequest.toStaticResources().atCommonLocations()
                );
        };
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
}