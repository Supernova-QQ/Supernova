package com.hanshin.supernova.security.jwt;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.auth.model.AuthUserImpl;
import com.hanshin.supernova.security.service.JwtService;
import com.hanshin.supernova.user.domain.Authority;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.hanshin.supernova.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//
////        // 로그인 요청의 경우 필터를 거치지 않도록 처리
////        String requestURI = request.getRequestURI();
////        if (requestURI.equals("/api/auth/login") || requestURI.equals("/api/users/all")) {
////            filterChain.doFilter(request, response);
////            return;
////        }
//
//        // AccessToken 헤더에서 가져오기
//        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_KEY);
//
//        // 토큰이 유효할 경우에만 사용자 정보를 추출
//        Claims claims = (accessToken != null) ? jwtService.getClaimsFromToken(accessToken) : null;
//
//        log.info("Claims : {}", claims);
//
//        if (claims != null) {
//            try{
//                Long userId = claims.get("userId", Long.class);
//                String email = claims.getSubject();
//                String role = claims.get("role", String.class);
//
//                AuthUser authUser = new AuthUserImpl(userId, email, null, Authority.valueOf(role));
//                request.setAttribute("authUser", authUser);  // AuthUser 자동 주입을 위한 코드
//                request.setAttribute("userEmail", email);     // 사용자 이메일을 위한 코드
//                request.setAttribute("userRole", role);       // 사용자 역할을 위한 코드
//
//                // 로그 추가
//                log.debug("AuthUser set: {}", authUser);
//                log.debug("userEmail set: {}", email);
//                log.debug("userRole set: {}", role);
//
//            } catch (ExpiredJwtException e) {
//                // 만료된 토큰 예외 처리
//                log.warn("JwtFilter) AccessToken expired: {}", e.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("AccessToken is expired");
//                return; // 요청 필터링 중단
//            } catch (JwtException e) {
//                // 유효하지 않은 토큰 예외 처리
//                log.warn("JwtFilter) Invalid AccessToken: {}", e.getMessage());
//                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                response.getWriter().write("Invalid AccessToken");
//                return; // 요청 필터링 중단
//            } catch (Exception e) {
//                // 기타 예외 처리
//                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//                response.getWriter().write("An unexpected error occurred while validating the token");
//                return; // 요청 필터링 중단
//            }
//        }
//
//        // 로그 추가: 토큰이 없을 때
//        if (claims == null) {
//            log.debug("No AccessToken found in request");
//        }
//
//        // 토큰이 없거나 예외가 발생하지 않은 경우 다음 필터로 진행
//        filterChain.doFilter(request, response);
//    }
//}


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        log.info("JwtFilter 실행됨");

        // 필터를 거치지 않도록 처리
        String requestURI = request.getRequestURI();

        log.info("requestURI: {}", requestURI);

        if (requestURI.equals("/") || requestURI.equals("/api/users/all") ||requestURI.equals("/api/auth/login") || requestURI.equals("/api/main/**")) {
            filterChain.doFilter(request, response);
            return;
        }

        // AccessToken 헤더에서 가져오기
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

        if (accessToken != null) {
            try{
                // 토큰이 유효할 경우에만 사용자 정보를 추출
                Claims claims = jwtService.getClaimsFromToken(accessToken);
                log.info("JWT Claims: {}", claims);

                Long userId = claims.get("userId", Long.class);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                AuthUser authUser = new AuthUserImpl(userId, email, null, Authority.valueOf(role));
                log.info("AuthUser 생성됨: {}", authUser);

                request.setAttribute("authUser", authUser);  // AuthUser 자동 주입을 위한 코드
                request.setAttribute("userEmail", email);     // 사용자 이메일을 위한 코드
                request.setAttribute("userRole", role);       // 사용자 역할을 위한 코드

            } catch (ExpiredJwtException e) {
                // 만료된 토큰 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("AccessToken is expired");
                return; // 요청 필터링 중단
            } catch (JwtException e) {
                // 유효하지 않은 토큰 예외 처리
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid AccessToken");
                return; // 요청 필터링 중단
            } catch (Exception e) {
                // 기타 예외 처리
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("An unexpected error occurred while validating the token");
                return; // 요청 필터링 중단
            }
        }

        // 로그 추가: 토큰이 없을 때
//        if (accessToken == null) {
//            log.info("AccessToken Request URI: {}", requestURI);
//            log.info("No AccessToken found in request");
////            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        }

        // 토큰이 없거나 예외가 발생하지 않은 경우 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}