package com.hanshin.supernova.security.service;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.auth.model.AuthUserImpl;
import com.hanshin.supernova.redis.service.RedisService;
import com.hanshin.supernova.user.domain.Authority;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

import static com.hanshin.supernova.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;
import static com.hanshin.supernova.auth.AuthConstants.REFRESH_TOKEN_HEADER_KEY;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${spring.security.jwt.secretKey}")
    private String SECRET_KEY;

    @Value("${spring.security.jwt.access.expiration}")
    private int accessTokenExpiration;

    @Getter
    private int accessTokenExpirationMinutes;

    @PostConstruct
    public void init() {
        // 초를 분으로 변환
        this.accessTokenExpirationMinutes = this.accessTokenExpiration / 60;
    }

    @Value("${spring.security.jwt.refresh.expiration}")
    private int refreshTokenExpiration;

    private SecretKey key;
    private final RedisService<String> redisService;  // RedisService 주입

    @Autowired
    public JwtService(@Value("${spring.security.jwt.secretKey}") String secretKey, RedisService<String> redisService) {
        this.redisService = redisService;
        this.SECRET_KEY = secretKey;

        // key 초기화 및 로그 확인
        if (SECRET_KEY != null)
            key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(360) // 1분의 시간 차이를 허용
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }


//    // JWT 토큰에서 Claims 추출
//    public Claims getClaimsFromToken(String token) {
//        return Jwts.parser()
//                .verifyWith(key)
//                .clockSkewSeconds(360) // 1분의 시간 차이를 허용
//                .build()
//                .parseSignedClaims(token)
//                .getPayload();
//    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .clockSkewSeconds(360) // 1분의 시간 차이를 허용
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("Token has expired: {}", e.getMessage());
            return null;
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT token: {}", e.getMessage());
            return null;
        } catch (SignatureException e) {
            log.warn("Invalid signature for JWT token: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("General JWT processing error: {}", e.getMessage());
            return null;
        }
    }

    // AccessToken 생성
    public String generateAccessToken(Long userId, String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("userId", userId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpirationMinutes * 1000L))
                .signWith(key)
                .compact();
    }

    // RefreshToken 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(key)
                .compact();
    }

    // AccessToken에서 사용자 정보를 추출하는 메서드
    public AuthUser getAuthUserFromToken(String accessToken) {
        Claims claims = getClaimsFromToken(accessToken);
        Long userId = claims.get("user_id", Long.class);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);

        return new AuthUserImpl(userId, email, "username_placeholder", Authority.valueOf(role));
    }

    // 로그아웃 시 AccessToken과 RefreshToken 제거 메서드
    public void remove(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractTokenFromRequest(request);

        if (refreshToken != null) {
            String email = getEmailFromToken(refreshToken);

            if (email != null && redisService.contains(email)) {
                redisService.delete(email);  // Redis에서 RefreshToken 삭제
            }

            // RefreshToken 쿠키에서 제거
            Cookie removeCookie = new Cookie(REFRESH_TOKEN_HEADER_KEY, null);
            removeCookie.setMaxAge(0);
            removeCookie.setHttpOnly(true);
            removeCookie.setSecure(true);
            response.addCookie(removeCookie);
        }

        // AccessToken 헤더에서 제거
        response.setHeader(ACCESS_TOKEN_HEADER_KEY, "");
    }

    // HttpServletRequest에서 RefreshToken을 추출하는 유틸리티 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 헤더에서 RefreshToken 추출 시도
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER_KEY);

        // 쿠키에서도 RefreshToken을 확인
        if (refreshToken == null || refreshToken.isBlank()) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (REFRESH_TOKEN_HEADER_KEY.equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return refreshToken;
    }

    // RefreshToken에서 이메일을 추출하는 메서드
    public String getEmailFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (JwtException e) {
            log.warn("Failed to extract email from token", e);
            return null;
        }
    }
}