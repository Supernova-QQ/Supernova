package com.hanshin.supernova.auth.presentation;

import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponse;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.redis.service.RedisService;
import com.hanshin.supernova.security.service.JwtService;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.hanshin.supernova.auth.AuthConstants.*;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService<String> redisService;  // Redis를 통한 RefreshToken 관리

    @Value("${spring.security.jwt.refresh.expiration}")
    private int refreshTokenExpiration;


    // 로그인 엔드포인트
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest loginRequest) {
        // 이메일로 사용자를 조회
        User user = userService.getByEmail(loginRequest.getEmail());

        // 비밀번호가 일치하는지 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthInvalidException(ErrorType.WRONG_PASSWORD_ERROR);
        }

        // 로그인 성공 시 AccessToken과 RefreshToken 발급
        String accessToken = jwtService.generateAccessToken(user.getEmail(), user.getAuthority().name());

        // RefreshToken 생성 및 Redis에 저장 (7일간 유효)
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        redisService.set(user.getEmail(), refreshToken, refreshTokenExpiration);  // Redis에 RefreshToken 저장

        // 헤더와 쿠키를 설정할 HttpHeaders 객체 생성
        HttpHeaders headers = new HttpHeaders();

        // RefreshToken을 쿠키에 추가
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_HEADER_KEY, refreshToken)
                .httpOnly(true)
                .secure(true)  // HTTPS를 사용하는 경우 true로 설정
                .path("/")  // 쿠키 경로 설정
                .maxAge(refreshTokenExpiration)  // 쿠키 만료 시간 설정
                .build();
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        // AccessToken과 RefreshToken을 클라이언트로 반환
        return ResponseEntity.ok()
                .headers(headers)
                .body(new AuthLoginResponse(user.getNickname(), accessToken, refreshToken));
    }


    // RefreshToken을 사용해 AccessToken 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request) {
        // 요청에서 RefreshToken 가져오기
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER_KEY);

        // RefreshToken이 없는 경우
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(400).body("Refresh token is missing");
        }

        // RefreshToken 유효성 검증
        if (!jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(403).body("Invalid Refresh Token");
        }

        // RefreshToken에서 사용자 이메일 추출
        String email = jwtService.getClaimsFromToken(refreshToken).getSubject();

        // Redis에서 해당 이메일의 RefreshToken 조회 (유효성 재검증)
        String storedRefreshToken = redisService.getValue(email);
        if (!refreshToken.equals(storedRefreshToken)) {
            return ResponseEntity.status(403).body("Refresh Token mismatch");
        }

        // 새로운 AccessToken 발급
        String newAccessToken = jwtService.generateAccessToken(email, "USER");  // 권한은 예시로 ROLE_USER 사용

        // 새 AccessToken 반환
        return ResponseEntity.ok(newAccessToken);
    }

//    // 로그아웃 엔드포인트 (변경 없음)
//    @PostMapping("/logout")
//    public ResponseEntity<?> logout(HttpServletRequest request) {
//        String email = request.getHeader(USER_EMAIL_HEADER_KEY);
//        redisService.delete(email);  // 로그아웃 시 Redis에서 RefreshToken 삭제
//        return ResponseEntity.ok("Logged out successfully");
//    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        // ACCESS_TOKEN_HEADER_KEY에서 AccessToken을 추출
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_KEY);

        try {
            // AccessToken에서 이메일 추출
            String email = jwtService.getClaimsFromToken(accessToken).getSubject();

            // Redis에서 RefreshToken 삭제
            redisService.delete(email);

            return ResponseEntity.ok("Logged out successfully");

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(ErrorType.EXPIRED_ACCESS_TOKEN.getStatus()).body("Access token is expired");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during logout");
        }
    }


}