package com.hanshin.supernova.auth.application;


import com.hanshin.supernova.auth.infrastructure.RefreshTokenRepository;
import com.hanshin.supernova.exception.auth.AuthorizationException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
@Service
@Configuration
@Profile("v3")
@RequiredArgsConstructor
public class TokenServiceV3 {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret.key}")
    private String key;

    private final long accessTokenValidity = 1000 * 60 * 15; // 15분
    private final long refreshTokenValidity = 1000 * 60 * 60 * 24 * 7; // 7일

    // 액세스 토큰 생성
    public String createAccessToken(Long id, String nickname) {
        Claims claims = Jwts.claims();
        claims.put("uid", id);
        claims.put("nickname", nickname);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidity); // 액세스 토큰 만료 시간 설정

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(Long userId) {
        String token = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
        return token;
    }

    // 액세스 토큰 검증
    public Long validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new AuthorizationException(ErrorType.NULL_TOKEN);
        }

        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes());

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            return Long.parseLong(claims.getSubject());
        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            throw new AuthorizationException(ErrorType.EXPIRED_TOKEN);
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }
    }
}