package com.hanshin.supernova.rate_limiter.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.rate_limiter.application.RateLimitService;
import com.hanshin.supernova.rate_limiter.dto.RateLimitStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 처리율 제한기 남은 횟수 조회 API
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/api/rate-limit/ai-answers")
@RequiredArgsConstructor
public class RateLimitController {

    private final RateLimitService rateLimitService;

    @Operation(summary = "AI 답변 생성 남은 횟수 조회")
    @GetMapping
    public ResponseEntity<RateLimitStatusResponse> getRateLimitStatus(
            AuthUser user
    ) {

        var response = rateLimitService.getRateLimitStatus(user);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "특정 게시글 내부 AI 답변 생성 남은 횟수 조회")
    @GetMapping("/questions/{questionId}")
    public ResponseEntity<RateLimitStatusResponse> getQuestionRateLimitStatus(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("questionId") Long questionId
    ) {

        var response = rateLimitService.getQuestionRateLimitStatus(user,
                questionId);

        return ResponseEntity.ok(response);
    }

}
