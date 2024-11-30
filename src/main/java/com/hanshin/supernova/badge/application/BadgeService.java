package com.hanshin.supernova.badge.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.Activity;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    /**
     * 멋진 질문자 배지를 부여하는 메서드
     * @param user 인증된 사용자 정보
     */
    @Transactional
    public void grantBookmarkedQuestionerBadge(AuthUser user) {
        log.info("\n\n\n\ngrantBookmarkedQuestionerBadge");
        // 1. AuthUser의 userId로 User 엔티티 조회
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 사용자가 작성한 질문 중 다른 사용자의 북마크에 10건 이상 추가된 질문 조회
        int bookmarkThreshold = 10; // 북마크 기준 수
        List<Long> bookmarkedQuestions = questionRepository.findBookmarkedQuestionsByUserId(foundUser.getId(), bookmarkThreshold);
        log.info("bookmarked questions: {}", bookmarkedQuestions);

        // 3. 북마크 기준을 충족하는 질문이 있으면 멋진 질문자 배지를 부여
        if (!bookmarkedQuestions.isEmpty()) {
            Activity activity = foundUser.getActivity();
            if (!activity.hasMarkedQuestionBadge()) {
                activity.setMarkedQuestionBadge(true);
                userRepository.save(foundUser); // 변경된 Activity 상태를 저장
            }
        }
    }

    /**
     * 인기 질문자 배지를 부여하는 메서드
     * @param user 인증된 사용자 정보
     */
    @Transactional
    public void grantPopularQuestionBadge(AuthUser user) {
        log.info("\n\n\n\ngrantPopularQuestionBadge");
        // 1. AuthUser의 userId로 User 엔티티 조회
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 추천 수가 10 이상인 질문을 조회
        int recommendationThreshold = 10; // 추천 수 기준
        int questionCountThreshold = 3; // 질문 개수 기준
        List<Long> popularQuestions = questionRepository.findPopularQuestionsByUserId(foundUser.getId(), recommendationThreshold);
        log.info("popular questions: {}", popularQuestions);

        // 3. 조건을 충족하는 경우 인기 질문자 배지를 부여
        if (popularQuestions.size() >= questionCountThreshold) {
            Activity activity = foundUser.getActivity();
            if (!activity.hasPopularQuestionBadge()) {
                activity.setPopularQuestionBadge(true);
                userRepository.save(foundUser); // 변경된 Activity 상태를 저장
            }
        }
    }

    /**
     * 정확한 답변자 배지를 부여하는 메서드
     * @param user 인증된 사용자 정보
     */
    @Transactional
    public void grantAcceptedAnswerBadge(AuthUser user) {
        log.info("\n\n\n\ngrantAcceptedAnswerBadge");
        // 1. AuthUser의 userId로 User 엔티티 조회
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 사용자가 작성한 답변 중 소스를 제공하고 채택된 답변을 조회
        int acceptedAnswerCountThreshold = 3; // 채택된 답변 수 기준
        List<Answer> acceptedAnswersWithSource = answerRepository.findAcceptedAnswersWithSourceByUserId(foundUser.getId());
        log.info("acceptedAnswerCountThreshold: {}", acceptedAnswersWithSource);

        // 3. 조건을 충족하는 경우 정확한 답변자 배지를 부여
        if (acceptedAnswersWithSource.size() >= acceptedAnswerCountThreshold) {
            Activity activity = foundUser.getActivity();
            if (!activity.hasAcceptedAnswerBadge()) {
                activity.setAcceptedAnswerBadge(true);
                userRepository.save(foundUser); // 변경된 Activity 상태를 저장
            }
        }
    }

    /**
     * 인기 답변자 배지를 부여하는 메서드
     * @param user 인증된 사용자 정보
     */
    @Transactional
    public void grantPopularAnswererBadge(AuthUser user) {
        log.info("\n\n\n\ngrantPopularAnswererBadge");
        // 1. AuthUser의 userId로 User 엔티티 조회
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 2. 사용자가 작성한 답변 중 소스를 제공하고 추천 수가 10 이상인 답변을 조회
        int recommendationThreshold = 10; // 추천 수 기준
        int answerCountThreshold = 3; // 답변 개수 기준
        List<Answer> popularAnswersWithSource = answerRepository.findPopularAnswersWithSourceByUserId(foundUser.getId(), recommendationThreshold);
        log.info("popularAnswersWithSource: {}", popularAnswersWithSource);

        // 3. 조건을 충족하는 경우 인기 답변자 배지를 부여
        if (popularAnswersWithSource.size() >= answerCountThreshold) {
            Activity activity = foundUser.getActivity();
            if (!activity.hasPopularAnswerBadge()) {
                activity.setPopularAnswerBadge(true);
                userRepository.save(foundUser); // 변경된 Activity 상태를 저장
            }
        }
    }
}
