package com.hanshin.supernova.security.config;

import com.hanshin.supernova.auth.service.CustomUserDetailsService;
import com.hanshin.supernova.security.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;  // CustomUserDetailsService 주입
    private final JwtFilter jwtFilter;  // JWT 필터 주입
    private final LogoutHandler logoutHandler;  // 로그아웃 핸들러 주입

    @Value("${spring.security.jwt.secretKey}")
    private String secretKey;

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // PasswordEncoder 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Pbkdf2PasswordEncoder(
                secretKey,
                16,
                310000,
                Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
    }

    // DaoAuthenticationProvider 빈 등록 (CustomUserDetailsService와 PasswordEncoder 설정)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);  // CustomUserDetailsService 사용
        authProvider.setPasswordEncoder(passwordEncoder());  // PasswordEncoder 사용
        return authProvider;
    }

    // SecurityFilterChain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(cnf -> cnf.ignoringRequestMatchers("/api/**"));

        http.logout(cnf -> {
            cnf.logoutUrl("/api/auth/logout");
            cnf.permitAll();
            cnf.addLogoutHandler(logoutHandler);
            cnf.logoutSuccessUrl("/");
        });

        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/api/auth/login").permitAll(); // 로그인 엔드포인트 허용 및 필터 제외
            auth.requestMatchers("/api/users/all").permitAll(); // 로그인 엔드포인트 허용 및 필터 제외
            auth.requestMatchers("/api/main").permitAll();
            auth.requestMatchers("/api/notices").permitAll();
            auth.requestMatchers("/api/news").permitAll();
            auth.requestMatchers("/api/communities/**").permitAll();
            auth.requestMatchers("/api/questions/**").permitAll();
            auth.requestMatchers("/api/users/**").permitAll();
            auth.requestMatchers("/api/auth/refresh").permitAll(); // RefreshToken 엔드포인트 허용
            auth.requestMatchers("/api/auth/**").anonymous();
        });

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }

    // RoleHierarchy 설정 (ROLE_ADMIN이 ROLE_USER보다 상위)
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return roleHierarchy;
    }
}
