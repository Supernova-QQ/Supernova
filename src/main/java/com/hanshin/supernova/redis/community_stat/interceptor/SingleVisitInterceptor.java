package com.hanshin.supernova.redis.community_stat.interceptor;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.security.application.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.hanshin.supernova.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;

/**
 * 방문자 정보 저장
 * 커뮤니티 정보에 접근하는 방문자가 있을 경우 해당 커뮤니티, 사용자, 날짜 정보를 조합하여 key 를 만듭니다.
 * redis 에 해당 정보를 저장합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SingleVisitInterceptor implements HandlerInterceptor {

    private final RedisTemplate<String, String> redisTemplate;
    private final CommunityRepository communityRepository;

    // TODO 만약 예원이가 한 내용 병합될 경우, TokenService -> SecurityTokenService
//    private final TokenService tokenService;
//    private final AuthUserResolver authUserResolver;
    private final JwtService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {

        String communityId = extractCommunityId(request);
        if (communityId == null) {
            return true; // 커뮤니티 ID를 찾지 못한 경우 그냥 통과
        } else if (!communityRepository.existsById(Long.parseLong(communityId))) {
            throw new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR);
        }

        // 토큰에서 AuthUser 정보 추출
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER_KEY);
        AuthUser authUser = null;
        if (accessToken != null && !accessToken.isEmpty()) {
//            AuthToken token = new AuthToken(accessToken);
            try {
//                authUser = tokenService.getAuthUser(token);
                authUser = jwtService.getAuthUserFromToken(accessToken);
            } catch (Exception e) {
                log.warn("Failed to get AuthUser from token", e);
            }
        }

        // 인증된 사용자가 없으면 IP 주소 사용
        String visitorIdentifier =
                (authUser != null) ? authUser.getId().toString() : request.getRemoteAddr();

        // IPv6 주소 처리
        if (visitorIdentifier.contains(":")) {
            // IPv6 주소의 ':' 를 '_'로 대체하여 Redis 키 구분자와 충돌 방지
            visitorIdentifier = visitorIdentifier.replace(":", "_");
        }

        String userAgent = request.getHeader("User-Agent");
        String today = LocalDate.now().toString();
        String key = "community:" + communityId + ":visit:" + visitorIdentifier + ":" + today;

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // redis에 방문 정보 저장
        if (!valueOperations.getOperations().hasKey(key)) {
            valueOperations.set(key, userAgent);
            log.info("New visit recorded: communityId={}, visitorIdentifier={}", communityId, visitorIdentifier);
        }

        return true;
    }

    // 요청에서 communityId 추출
    private String extractCommunityId(HttpServletRequest request) {
        String path = request.getRequestURI();
        Pattern pattern = Pattern.compile("/api/communities/(\\d+)");
        Matcher matcher = pattern.matcher(path);
        return matcher.find() ? matcher.group(1) : null;
    }
}
