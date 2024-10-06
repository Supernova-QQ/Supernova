package com.hanshin.supernova.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/api/security/auth/login", "/api/security/auth/logout", "/api/security/users").permitAll() // 공개 URL
                        .anyRequest().authenticated()) // 인증이 필요한 주소
                .formLogin(login -> login
                        .loginPage("/loginForm") // 로그인 페이지 설정
                        .loginProcessingUrl("/login") // 로그인 처리 URL 설정
                        .defaultSuccessUrl("/") // 로그인 성공 후 이동할 주소 설정
                        .permitAll()) // 모든 사용자 접근 허용
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 URL 설정
                        .logoutSuccessUrl("/") // 로그아웃 후 이동할 주소 설정
                        .invalidateHttpSession(true) // 세션 무효화
                        .permitAll()); // 모든 사용자 접근 허용
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 패스워드 암호화
    }
}
