package com.hanshin.supernova.auth.application;

import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponse;
import com.hanshin.supernova.auth.infrastructure.RefreshTokenRepository;
import com.hanshin.supernova.auth.model.RefreshToken;
import com.hanshin.supernova.exception.auth.AuthorizationException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@Profile("v3")
@RequiredArgsConstructor
public class AuthServiceImplV3 implements AuthService {
    private final UserRepository userRepository;
    private final TokenServiceV3 tokenService; // 토큰 처리 전담 서비스
    private final RefreshTokenRepository refreshTokenRepository; // 리프레시 토큰 저장소
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    /**
     * 로그인 시 액세스 토큰과 리프레시 토큰을 발급한다.
     */
    public AuthLoginResponse login(AuthLoginRequest request) {
        // 유저 정보 인증
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthorizationException(ErrorType.AUTHORIZATION_ERROR));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthorizationException(ErrorType.INVALID_PASSWORD);
        }

        // 액세스 토큰과 리프레시 토큰 생성
        String accessToken = tokenService.createAccessToken(user.getId(), user.getNickname());
        String refreshToken = tokenService.createRefreshToken(user.getId());

        // 리프레시 토큰을 DB에 저장
        RefreshToken refreshTokenEntity = new RefreshToken(user.getId(), refreshToken, new Date().getTime() + refreshTokenExpiration * 1000, user);
        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthLoginResponse(user.getNickname(), accessToken, refreshToken);
    }

    /**
     * 리프레시 토큰으로 액세스 토큰 재발급
     */
    public AuthLoginResponse refreshToken(String refreshToken) {
        // 리프레시 토큰 검증
        Long userId = tokenService.validateRefreshToken(refreshToken);

        // 리프레시 토큰이 유효한지 확인
        Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByUserId(userId);
        if (storedRefreshToken.isEmpty() || !storedRefreshToken.get().getToken().equals(refreshToken)) {
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }

        // 새로운 액세스 토큰 발급
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthorizationException(ErrorType.AUTHORIZATION_ERROR));
        String newAccessToken = tokenService.createAccessToken(userId, user.getNickname());

        return new AuthLoginResponse(user.getNickname(), newAccessToken, refreshToken);
    }

    /**
     * 로그아웃 처리 (리프레시 토큰 삭제)
     */
    public void logout(String token) {
        try {
            // 여기서 validateToken 메서드를 호출하여 클레임을 얻습니다.
            Jws<Claims> claims = validateToken(token);

            // 클레임에서 특정 키를 검사하여 액세스 토큰인지 리프레시 토큰인지 확인
            if (claims.getBody().containsKey("uid")) {
                // 액세스 토큰 처리
                Long userId = claims.getBody().get("uid", Long.class);
                // 로그아웃 로직
                refreshTokenRepository.deleteByUserId(userId);
                log.info("Successfully logged out user with id: " + userId);
            } else {
                throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
            }
        } catch (AuthorizationException e) {
            log.error("Logout failed: " + e.getMessage());
            throw new AuthorizationException(ErrorType.EXPIRED_TOKEN);
        }
    }

    // validateToken 메서드
    public Jws<Claims> validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes())) // Deprecated 메서드 대신 사용
                    .build()
                    .parseClaimsJws(token);
        } catch (Exception e) {
            log.error("Invalid token: " + e.getMessage());
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }
    }
}