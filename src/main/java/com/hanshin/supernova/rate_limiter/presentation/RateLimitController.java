package com.hanshin.supernova.rate_limiter.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.rate_limiter.application.APIRateLimiter;
import com.hanshin.supernova.rate_limiter.dto.RateLimitStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 처리율 제한기 남은 횟수 조회 API
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/api/rate-limit")
@RequiredArgsConstructor
public class RateLimitController {

    private final APIRateLimiter apiRateLimiter;

    /**
     * AI 답변 생성 남은 횟수 조회
     */
    @GetMapping("/ai-answers")
    public ResponseEntity<RateLimitStatusResponse> getRateLimitStatus(
            AuthUser user
    ) {
        long remainingGenerate = apiRateLimiter.getRemainingTokens(
                "createAIAnswerTest:user" + user.getId(),
                5,
                24 * 60 * 60
        );

        return ResponseEntity.ok(
                new RateLimitStatusResponse(remainingGenerate));
    }

}
