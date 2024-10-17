package com.hanshin.supernova.auth.application;

import com.hanshin.supernova.auth.model.AuthToken;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.auth.model.AuthUserImpl;
import com.hanshin.supernova.exception.auth.AuthorizationException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.hanshin.supernova.auth.AuthCostants.AUTH_TOKEN_HEADER_KEY;

@Slf4j
@Component
@RequiredArgsConstructor

public class TokenService {
    // private final long accessTokenValidMillisecond = 1000L * 60 * 100000; // AccessToken 30초 토큰
    // 유효
    private final UserRepository userRepository;
    private String key;
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    @Value("${security.jwt.secret.key}")
    public void getSecretKey(String secretKey) {
        log.info("secret key {}", secretKey);
        key = secretKey;
    }

    public void verifyToken(String token) {
        if (isTokenBlacklisted(token)) {
            throw new AuthorizationException(ErrorType.TOKEN_BLACKLISTED);
        }
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key.getBytes()).parseClaimsJws(token);
        } catch (Exception e) {
            if (e.getMessage().contains("JWT expired")) {
                throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
            }
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }
        Long uid = getUserIdFromToken(token);
        if (!userRepository.existsById(uid)) {
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }
    }

    public AuthUser getAuthUser(AuthToken token) {
        verifyToken(token.getToken());
        var id = getUserIdFromToken(token.getToken());
        var user =
                userRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new AuthorizationException(ErrorType.AUTHORIZATION_ERROR));
        return new AuthUserImpl(id);
    }

    public Long getUserIdFromToken(String token) {
        return Long.valueOf(
                (Integer)
                        Jwts.parser()
                                .setSigningKey(key.getBytes())
                                .parseClaimsJws(token)
                                .getBody()
                                .get("uid"));
    }

    public String jwtBuilder(Long id, String nickname) {
        Claims claims = Jwts.claims();
        claims.put("nickname", nickname);
        claims.put("uid", id);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                // TODO : 유효기간 설정은 다음 MVP에서 진행한다.
                // .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    public void logout(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            verifyToken(token);
            blacklistedTokens.add(token);
            log.info("Token blacklisted: {}", token);
        } catch (Exception e) {
            log.error("Error during logout", e);
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

}