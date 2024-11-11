package com.hanshin.supernova.badge.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.application.UserServiceImpl;
import com.hanshin.supernova.user.domain.Activity;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;


    /**
     * 멋진 질문자 배지를 부여하는 메서드
     * @param request JwtFilter에서 추출된 사용자 Claims를 담은 request
     */
    @Transactional
    public void grantBookmarkedQuestionerBadge(HttpServletRequest request) {

        // 1. Claims에서 추출한 사용자 이메일을 User 엔티티에서 조회한 이메일을 가져옴
        User user = userService.getUserFromClaims(request);

        // 2. 사용자가 작성한 질문이 다른 사용자의 북마크에 10건 이상 추가되었는지 조회
        int bookmarkThreshold = 10; // 북마크 기준 수
        List<Long> bookmarkedQuestions = questionRepository.findBookmarkedQuestionsByUserId(user.getId(), bookmarkThreshold);

        // 3. 조건을 충족하면 멋진 질문자 배지를 부여
        if (bookmarkedQuestions.size() >= 1) {
            Activity activity = user.getActivity();
            if (!activity.hasMarkedQuestionBadge()) {
                activity.setMarkedQuestionBadge(true);
                userRepository.save(user); // 변경된 Activity 상태를 저장
            }
        }
    }

    /**
     * 인기 질문자 배지를 부여하는 메서드
     * @param request JwtFilter에서 추출된 사용자 Claims를 담은 request
     */
    @Transactional
    public void grantPopularQuestionBadge(HttpServletRequest request) {

        // 1. Claims에서 추출한 사용자 이메일을 User 엔티티에서 조회한 이메일을 가져옴
        User user = userService.getUserFromClaims(request);

        // 2. 추천 수가 10 이상인 질문을 3개 이상 가지고 있는지 조회
        int recommendationThreshold = 10; // 추천 수 기준
        int questionCountThreshold = 3; // 질문 개수 기준
        List<Long> popularQuestions = questionRepository.findPopularQuestionsByUserId(user.getId(), recommendationThreshold);

        // 4. 조건을 충족하면 인기 질문자 배지를 부여
        if (popularQuestions.size() >= questionCountThreshold) {
            Activity activity = user.getActivity();
            if (!activity.hasPopularQuestionBadge()) {
                activity.setPopularQuestionBadge(true);
                userRepository.save(user); // 변경된 Activity 상태를 저장
            }
        }
    }

    /**
     * 정확한 답변자 배지를 부여하는 메서드
     * @param request JwtFilter에서 추출된 사용자 Claims를 담은 request
     */
    public void grantAcceptedAnswerBadge(HttpServletRequest request) {

        // 1. Claims에서 추출한 사용자 이메일을 User 엔티티에서 조회한 이메일을 가져옴
        User user = userService.getUserFromClaims(request);

        // 2. 사용자가 소스를 제공한 답변 중 채택된 답변 수가 3개 이상인지 조회
        int acceptedAnswerCountThreshold = 3; // 채택된 답변 수 기준
        List<Answer> acceptedAnswersWithSource = answerRepository.findAcceptedAnswersWithSourceByUserId(user.getId());


        // 3. 조건을 충족하면 정확한 답변자 배지를 부여
        if (acceptedAnswersWithSource.size() >= acceptedAnswerCountThreshold) {
            Activity activity = user.getActivity();
            if (!activity.hasAcceptedAnswerBadge()){
                activity.setAcceptedAnswerBadge(true);
                userRepository.save(user);
            }
        }
    }

    /**
     * 인기 답변자 배지를 부여하는 메서드
     * @param request JwtFilter에서 추출된 사용자 Claims를 담은 request
     */
    @Transactional
    public void grantPopularAnswererBadge(HttpServletRequest request) {

        // 1. Claims에서 추출한 사용자 이메일을 User 엔티티에서 조회한 이메일을 가져옴
        User user = userService.getUserFromClaims(request);

        // 2. 사용자가 소스를 제공한 답변 중 추천 수가 10 이상인 답변이 3개 이상인지 조회
        int recommendationThreshold = 10; // 추천 수 기준
        int answerCountThreshold = 3; // 답변 개수 기준
        List<Answer> popularAnswersWithSource = answerRepository.findPopularAnswersWithSourceByUserId(user.getId(), recommendationThreshold);

        // 3. 조건을 충족하면 인기 답변자 배지를 부여
        if (popularAnswersWithSource.size() >= answerCountThreshold) {
            Activity activity = user.getActivity();
            if (!activity.hasPopularAnswerBadge()) {
                activity.setPopularAnswerBadge(true);
                userRepository.save(user); // 변경된 Activity 상태를 저장
            }
        }
    }
}
