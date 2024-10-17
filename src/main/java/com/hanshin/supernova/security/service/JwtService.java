package com.hanshin.supernova.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.redis.service.RedisService;
import com.hanshin.supernova.security.RandomStringGenerator;
import com.hanshin.supernova.security.model.AccessTokenWrapper;
import com.hanshin.supernova.security.model.AuthorizeToken;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import static com.hanshin.supernova.security.TokenConstants.*;

@Slf4j
@Service
public class JwtService {

    private final UserService userService;
    private final RedisService<String> redisService;
    private final SecretKey secretKey;
    private final JwtParser secretParser;
    private final JwtParser claimParser;
    private final ObjectMapper objectMapper;


    @Value("${spring.security.jwt.issuer}")
    private String issuerUrl;

    @Value("${security.jwt.access.expiration")
    private int accessTokenExpiration;

    @Value("${security.jwt.refresh.expiration")
    private int refreshTokenExpiration;


    @Autowired
    public JwtService(UserService userService, RedisService<String> jwtRedisService, @Value("${spring.security.jwt.secretKey}") String secretKey, ObjectMapper objectMapper) {
        this.userService = userService;
        this.redisService = jwtRedisService;
        this.secretKey = Keys.password(secretKey.toCharArray());
        this.objectMapper = objectMapper;
        this.secretParser = Jwts.parser().verifyWith(this.secretKey).build();
        this.claimParser = Jwts.parser().build();
    }

    @Nullable
    public Long getUserId(String accessToken) {
        try {
            Jws<Claims> claims = claimParser.parseSignedClaims(accessToken);
            return claims.getPayload().get(USER_ID_CLAIM, Long.class);
        } catch (JwtException e) {
            return null;
        } catch (Exception e) {
            log.warn("Unexpected exception threw while read claim", e);
            throw e;
        }
    }

    public String generateAccessToken(User user) {
        Instant issuedAt = Instant.now();
        Instant expiredAt = issuedAt.plusSeconds(accessTokenExpiration);

        ClaimsBuilder claimsBuilder = Jwts.claims();
        claimsBuilder.subject(user.getUsername());
        claimsBuilder.add(USER_ID_CLAIM, user.getId());
        claimsBuilder.add(USER_EMAIL_CLAIM, user.getEmail());
        claimsBuilder.add(USER_AUTHORITY_CLAIM, user.getAuthority().toString());
        claimsBuilder.issuedAt(Date.from(issuedAt));
        claimsBuilder.notBefore(Date.from(issuedAt));
        claimsBuilder.issuer(issuerUrl);
        claimsBuilder.expiration(Date.from(expiredAt));
        Claims claims = claimsBuilder.build();

        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }

    public String getRefreshToken() {
        final int refreshTokenLength = 16;
        return RandomStringGenerator.generateRandomString(refreshTokenLength);
    }

    public void setTokenPair(AuthorizeToken authorizeToken) {
        redisService.set(getPairTokenKey(authorizeToken.getRefreshToken()), authorizeToken.getAccessToken(), refreshTokenExpiration);
    }

    public String refresh(AuthorizeToken authorizeToken) {
        final String accessToken = authorizeToken.getAccessToken();
        final String refreshToken = authorizeToken.getRefreshToken();

        if (isUsedAccessToken(accessToken)) {
            throw new AuthInvalidException(ErrorType.USED_ACCESS_TOKEN_ERROR);
        }

        if (isUsedRefreshToken(refreshToken)) {
            throw new AuthInvalidException(ErrorType.USED_REFRESH_TOKEN_ERROR);
        }

        String storedAccessToken = redisService.getValue(getPairTokenKey(refreshToken));
        if (!accessToken.equals(storedAccessToken)) {
            throw new AuthInvalidException(ErrorType.REFRESH_ACCESS_TOKEN_NOT_MATCH_ERROR);
        }

        Claims claims;
        try {
            claims = secretParser.parseSignedClaims(accessToken).getPayload();
        } catch (JwtException e) {
            if (e instanceof ExpiredJwtException expired) {
                claims = expired.getClaims();
            } else {
                throw new AuthInvalidException(ErrorType.INVALID_ACCESS_TOKEN_ERROR);
            }
        }

        storeUsedAccessToken(accessToken);
        storeUsedRefreshToken(refreshToken);
        Long userId = claims.get(USER_ID_CLAIM, Long.class);
        User user = userService.getById(userId);
        return generateAccessToken(user);
    }

    @Nullable
    public Claims validateAccessToken(String accessToken) {
        try {
            if (isUsedAccessToken(accessToken)) {
                throw new AuthInvalidException(ErrorType.USED_ACCESS_TOKEN_ERROR);
            }
            return secretParser.parseSignedClaims(accessToken).getPayload();
        } catch (JwtException e) {
            return null;
        }
    }

    public void writeResponse(HttpServletResponse response, AuthorizeToken authorizeToken) throws IOException {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, authorizeToken.getRefreshToken());
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(refreshTokenExpiration);
        cookie.setPath("/");
        response.addCookie(cookie);

        AccessTokenWrapper wrapper = new AccessTokenWrapper(authorizeToken.getAccessToken());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), wrapper);
    }

    public void remove(HttpServletRequest request, HttpServletResponse response) {
        Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(REFRESH_TOKEN_COOKIE_NAME))
                .findFirst()
                .ifPresent(c -> {
                    storeUsedRefreshToken(c.getValue());
                    redisService.delete(getPairTokenKey(c.getValue()));
                });

        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    private boolean isUsedAccessToken(String accessToken) {
        return redisService.contains(getUsedAccessTokenKey(accessToken));
    }

    private boolean isUsedRefreshToken(String refreshToken) {
        return redisService.contains(getUsedRefreshTokenKey(refreshToken));
    }

    private void storeUsedAccessToken(String accessToken) {
        redisService.set(getUsedAccessTokenKey(accessToken), "", refreshTokenExpiration);
    }

    private void storeUsedRefreshToken(String refreshToken) {
        redisService.set(getUsedRefreshTokenKey(refreshToken), "", refreshTokenExpiration);
    }

    private String getUsedAccessTokenKey(String accessToken) {
        return "used_a_" + accessToken.hashCode();
    }

    private String getUsedRefreshTokenKey(String refreshToken) {
        return "used_r_" + refreshToken;
    }

    private String getPairTokenKey(String refreshToken) {
        return "pair_" + refreshToken;
    }
}
