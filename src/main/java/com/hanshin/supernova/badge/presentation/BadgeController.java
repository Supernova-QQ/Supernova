package com.hanshin.supernova.badge.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.badge.application.BadgeService;
import com.hanshin.supernova.user.domain.Activity;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;
    private final UserRepository userRepository;

    @Operation(summary = "사용자의 배지 상태를 반환")
    @GetMapping
    public ResponseEntity<Activity> getUserBadgeStatus(AuthUser authUser) {
        // 1. AuthUser의 userId로 User 엔티티 조회
        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 배지 부여 로직 실행
        badgeService.grantBookmarkedQuestionerBadge(authUser);
        badgeService.grantPopularQuestionBadge(authUser);
        badgeService.grantAcceptedAnswerBadge(authUser);
        badgeService.grantPopularAnswererBadge(authUser);

        // 3. User의 Activity(배지 상태) 반환
        Activity activity = user.getActivity();

        log.info("BadgeController Activity: {}", activity);

        return ResponseEntity.ok(activity);
    }
}