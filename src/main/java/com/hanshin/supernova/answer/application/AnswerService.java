package com.hanshin.supernova.answer.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.domain.AnswerRecommendation;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.answer.infrastructure.AnswerRecommendationRepository;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.validation.AnswerValidator;
import com.hanshin.supernova.validation.AuthenticationUtils;
import com.hanshin.supernova.validation.QuestionValidator;
import com.hanshin.supernova.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerService {
    private final QuestionValidator questionValidator;
    private final UserValidator userValidator;
    private final AnswerValidator answerValidator;

    private final AnswerRepository answerRepository;
    private final AnswerRecommendationRepository answerRecommendationRepository;

    /**
     * 답변 생성
     */
    @Transactional
    public AnswerResponse createAnswer(AuthUser user, Long qId, AnswerRequest request) {
        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        Answer answer = buildAnswer(qId, request, findUser.getId());
        Answer savedAnswer = answerRepository.save(answer);

        findQuestion.increaseAnswerCnt();

        return getAnswerResponse(savedAnswer, findUser);
    }

    /**
     * 답변 조회
     */
    @Transactional(readOnly = true)
    public AnswerResponse getAnswer(AuthUser user, Long qId, Long aId) {
        questionValidator.getQuestionOrThrowIfNotExist(qId);

        Answer findAnswer = answerValidator.getAnswerOrThrowIfNotExist(aId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        return getAnswerResponse(findAnswer, findUser);
    }

    /**
     * 답변 수정
     */
    @Transactional
    public AnswerResponse editAnswer(AuthUser user, Long qId, Long aId, AnswerRequest request) {
        questionValidator.getQuestionOrThrowIfNotExist(qId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        Answer findAnswer = answerValidator.getAnswerOrThrowIfNotExist(aId);

        AuthenticationUtils.verifySameUser(findAnswer.getAnswererId(), findUser.getId());

        // 채택된 답변은 수정이 불가능
        if (findAnswer.isAccepted()) {
            throw new AnswerInvalidException(ErrorType.ACCEPTED_ANSWER_CANNOT_BE_EDITED_ERROR);
        }

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
        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        Answer findAnswer = answerValidator.getAnswerOrThrowIfNotExist(aId);

        AuthenticationUtils.verifySameUser(findAnswer.getAnswererId(), findUser.getId());

        // 채택된 답변은 삭제 불가능
        if (findAnswer.isAccepted()) {
            throw new AnswerInvalidException(ErrorType.ACCEPTED_ANSWER_CANNOT_BE_DELETED_ERROR);
        }

        answerRepository.deleteById(findAnswer.getId());
        findQuestion.decreaseAnswerCnt();

        return new SuccessResponse("답변 삭제를 성공했습니다.");
    }

    /**
     * 답변 전체 목록
     */
    public List<AnswerResponse> getAnswerList(Long qId) {
        questionValidator.getQuestionOrThrowIfNotExist(qId);

        List<AnswerResponse> answerResponses = new ArrayList<>();
        List<Answer> findAnswers = answerRepository.findAllByQuestionId(qId);

        findAnswers.forEach(findAnswer -> {
            User answerer = userValidator.getUserOrThrowIfNotExist(findAnswer.getAnswererId());
            answerResponses.add(getAnswerResponse(findAnswer, answerer));
        });

        return answerResponses;
    }


    /**
     * 답변 채택
     */
    @Transactional
    public AnswerResponse acceptAnswer(AuthUser user, Long qId, Long aId) {
        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        Answer findAnswer = answerValidator.getAnswerOrThrowIfNotExist(aId);

        // 자신이 등록한 댓글 채택하는 것 방지
        if (findUser.getId().equals(findAnswer.getAnswererId())) {
            throw new AnswerInvalidException(
                    ErrorType.AUTHOR_CANNOT_ACCEPT_THEIR_OWN_ANSWER_ERROR);
        }

        // 질문 당사자가 아닌 사람이 댓글 채택하는 것 방지
        if (!findUser.getId().equals(findQuestion.getQuestionerId())) {
            throw new AnswerInvalidException(
                    ErrorType.ONLY_QUESTIONER_CAN_ADOPT_ANSWER_ERROR);
        }

        findAnswer.changeStatus();
        findQuestion.changeResolveStatus();

        return getAnswerResponse(findAnswer, findUser);
    }

    @Transactional
    public AnswerResponse updateAnswerRecommendation(AuthUser user, Long aId) {
        Answer findAnswer = answerValidator.getAnswerOrThrowIfNotExist(aId);
        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());
        // 자신의 응답은 추천하지 못하도록 예외처리
        if (findAnswer.getAnswererId().equals(findUser.getId())) {
            throw new AuthInvalidException(ErrorType.WRITER_CANNOT_RECOMMEND_ERROR);
        }
        // 기존 추천한 이력 유무에 따른 추천수 증감
        AnswerRecommendation answerRecommendation = answerRecommendationRepository.findByAnswerIdAndRecommenderId(
                findAnswer.getId(), findUser.getId());
        if (answerRecommendation == null) {
            answerRecommendationRepository.save(AnswerRecommendation.builder()
                    .recommendedAt(LocalDate.now())
                    .answerId(findAnswer.getId())
                    .recommenderId(findUser.getId())
                    .build());

            findAnswer.increaseRecommendationCnt();
        } else {
            answerRecommendationRepository.deleteById(answerRecommendation.getId());
            findAnswer.decreaseRecommendationCnt();
        }

        return AnswerResponse.toResponse(
                findAnswer.getId(),
                findUser.getNickname(),
                findAnswer.getAnswer(),
                findAnswer.getCreatedAt(),
                findAnswer.getRecommendationCnt(),
                findAnswer.getTag(),
                findAnswer.getSource(),
                findAnswer.isAi(),
                findAnswer.isAccepted()
        );
    }



    private static AnswerResponse getAnswerResponse(Answer answer, User user) {
        return AnswerResponse.toResponse(
                answer.getId(),
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
}