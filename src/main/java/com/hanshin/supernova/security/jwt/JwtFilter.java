package com.hanshin.supernova.security.jwt;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.auth.model.AuthUserImpl;
import com.hanshin.supernova.security.service.JwtService;
import com.hanshin.supernova.user.domain.Authority;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.hanshin.supernova.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

//        // 로그인 요청의 경우 필터를 거치지 않도록 처리
//        String requestURI = request.getRequestURI();
//        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/users/all")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        // AccessToken 헤더에서 가져오기
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

        // 토큰이 유효할 경우에만 사용자 정보를 추출
        Claims claims = (accessToken != null) ? jwtService.getClaimsFromToken(accessToken) : null;

        if (claims != null) {
            Long userId = claims.get("userId", Long.class);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            AuthUser authUser = new AuthUserImpl(userId, email, null, Authority.valueOf(role));
            request.setAttribute("authUser", authUser);  // AuthUser 자동 주입을 위한 코드
            request.setAttribute("userEmail", email);     // 사용자 이메일을 위한 코드
            request.setAttribute("userRole", role);       // 사용자 역할을 위한 코드

        }
        filterChain.doFilter(request, response);
    }
}
