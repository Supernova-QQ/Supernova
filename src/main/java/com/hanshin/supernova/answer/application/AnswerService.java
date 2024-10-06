package com.hanshin.supernova.answer.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    /**
     * 답변 생성
     */
    @Transactional
    public AnswerResponse createAnswer(AuthUser user, Long qId, AnswerRequest request) {
        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        Answer answer = buildAnswer(qId, request, findUser.getId());
        Answer savedAnswer = answerRepository.save(answer);

//        findQuestion.increaseAnswerCnt();

        return getAnswerResponse(savedAnswer, findUser);
    }

    /**
     * 답변 조회
     */
    @Transactional(readOnly = true)
    public AnswerResponse getAnswer(AuthUser user, Long qId, Long aId) {
        getQuestionById(qId);

        Answer findAnswer = getAnswerById(aId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        return getAnswerResponse(findAnswer, findUser);
    }

    /**
     * 답변 수정
     */
    @Transactional
    public AnswerResponse editAnswer(AuthUser user, Long qId, Long aId, AnswerRequest request) {
        getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        Answer findAnswer = getAnswerById(aId);

        validateSameUser(findAnswer, findUser.getId());

        findAnswer.updateAnswer(
                request.getAnswer(),
                request.getTag(),
                request.getSource()
        );

        return getAnswerResponse(findAnswer, findUser);
    }

    /**
     * 답변 삭제
     */
    @Transactional
    public SuccessResponse deleteAnswer(AuthUser user, Long qId, Long aId) {
        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        Answer findAnswer = getAnswerById(aId);

        validateSameUser(findAnswer, findUser.getId());

        answerRepository.deleteById(findAnswer.getId());
//        findQuestion.decreaseAnswerCnt();

        return new SuccessResponse("답변 삭제를 성공했습니다.");
    }

    /**
     * 답변 전체 목록
     */
    public List<AnswerResponse> getAnswerList(Long qId) {
        getQuestionById(qId);

        List<AnswerResponse> answerResponses = new ArrayList<>();
        List<Answer> findAnswers = answerRepository.findAllByQuestionId(qId);

        findAnswers.forEach(findAnswer -> {
            User answerer = getUserOrThrowIfNotExist(findAnswer.getAnswererId());
            answerResponses.add(getAnswerResponse(findAnswer, answerer));
        });

        return answerResponses;
    }


    /**
     * 답변 채택
     */
    @Transactional
    public AnswerResponse acceptAnswer(AuthUser user, Long qId, Long aId) {
        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        Answer findAnswer = getAnswerById(aId);
        findAnswer.changeStatus();
        findQuestion.changeStatus();

        return getAnswerResponse(findAnswer, findUser);
    }

//    /**
//     * 답변 개수
//     */
//    @Transactional(readOnly = true)
//    public QuestionInfoResponse getAnswerCnt(Long qId) {
//        Question findQuestion = getQuestionById(qId);
//
//        return QuestionInfoResponse.toResponse(
//                findQuestion.getId(),
//                findQuestion.getTitle(),
//                findQuestion.getContent(),
//                findQuestion.getAnswerCnt()
//        );
//    }


    private User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
    }

    private static AnswerResponse getAnswerResponse(Answer answer, User user) {
        return AnswerResponse.toResponse(
                answer.getAnswererId(),
                user.getNickname(),
                answer.getAnswer(),
                answer.getCreatedAt(),
                answer.getRecommendationCnt(),
                answer.getTag(),
                answer.getSource(),
                answer.isAi(),
                answer.isAccepted()
        );
    }

    private Question getQuestionById(Long qId) {
        return questionRepository.findById(qId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }

    private static Answer buildAnswer(Long qId, AnswerRequest request, Long userId) {
        return Answer.builder()
                .answer(request.getAnswer())
                .tag(request.getTag())
                .source(request.getSource())
                .isAi(false)
                .isAccepted(false)
                .recommendationCnt(0)
                .answererId(userId)
                .questionId(qId)
                .build();
    }

    private Answer getAnswerById(Long aId) {
        return answerRepository.findById(aId).orElseThrow(
                () -> new AnswerInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR)
        );
    }

    private static void validateSameUser(Answer findAnswer, Long userId) {
        if (!findAnswer.getAnswererId().equals(userId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }
}
