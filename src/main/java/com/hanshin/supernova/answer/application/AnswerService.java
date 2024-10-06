package com.hanshin.supernova.answer.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.response.QuestionInfoResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
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

    /**
     * 답변 생성
     */
    @Transactional
    public AnswerResponse createAnswer(Long qId, AnswerRequest request) {
        Question findQuestion = getQuestionById(qId);

        Long userId = 1L;    // TODO user 정보 등록
        String nickname = "tester";

        Answer answer = buildAnswer(qId, request, userId);
        Answer savedAnswer = answerRepository.save(answer);

        findQuestion.increaseAnswerCnt();

        return AnswerResponse.toResponse(
                savedAnswer.getAnswererId(),
                nickname,
                savedAnswer.getAnswer(),
                savedAnswer.getCreatedAt(),
                savedAnswer.getRecommendationCnt(),
                savedAnswer.getTag(),
                savedAnswer.getSource(),
                savedAnswer.isAi(),
                savedAnswer.isAccepted()
        );
    }

    /**
     * 답변 조회
     */
    @Transactional(readOnly = true)
    public AnswerResponse getAnswer(Long qId, Long aId) {
        getQuestionById(qId);

        Answer findAnswer = getAnswerById(aId);

        String nickname = "tester"; // TODO user 정보 등록

        return AnswerResponse.toResponse(
                findAnswer.getAnswererId(),
                nickname,
                findAnswer.getAnswer(),
                findAnswer.getCreatedAt(),
                findAnswer.getRecommendationCnt(),
                findAnswer.getTag(),
                findAnswer.getSource(),
                findAnswer.isAi(),
                findAnswer.isAccepted()
        );
    }

    /**
     * 답변 수정
     */
    @Transactional
    public AnswerResponse editAnswer(Long qId, Long aId, AnswerRequest request) {
        getQuestionById(qId);

        Long answererId = 1L;   // TODO user 정보
        String nickname = "tester";

        Answer findAnswer = getAnswerById(aId);

        validateSameUser(findAnswer, answererId);

        findAnswer.updateAnswer(
                request.getAnswer(),
                request.getTag(),
                request.getSource()
        );

        return AnswerResponse.toResponse(
                findAnswer.getAnswererId(),
                nickname,
                findAnswer.getAnswer(),
                findAnswer.getCreatedAt(),
                findAnswer.getRecommendationCnt(),
                findAnswer.getTag(),
                findAnswer.getSource(),
                findAnswer.isAi(),
                findAnswer.isAccepted()
        );
    }

    /**
     * 답변 삭제
     */
    @Transactional
    public SuccessResponse deleteAnswer(Long qId, Long aId) {
        Question findQuestion = getQuestionById(qId);

        Long answererId = 1L;   // TODO user 정보

        Answer findAnswer = getAnswerById(aId);

        validateSameUser(findAnswer, answererId);

        answerRepository.deleteById(findAnswer.getId());
        findQuestion.decreaseAnswerCnt();

        return new SuccessResponse("답변 삭제를 성공했습니다.");
    }

    /**
     * 답변 전체 목록
     */
    public List<AnswerResponse> getAnswerList(Long qId) {
        getQuestionById(qId);

        List<AnswerResponse> answerResponses = new ArrayList<>();
        List<Answer> findAnswers = answerRepository.findAllByQuestionId(qId);

        String nickname = "tester"; // TODO user 정보 등록

        findAnswers.forEach(findAnswer -> {
            answerResponses.add(AnswerResponse.toResponse(
                    findAnswer.getAnswererId(),
                    nickname,
                    findAnswer.getAnswer(),
                    findAnswer.getCreatedAt(),
                    findAnswer.getRecommendationCnt(),
                    findAnswer.getTag(),
                    findAnswer.getSource(),
                    findAnswer.isAi(),
                    findAnswer.isAccepted()
            ));
        });

        return answerResponses;
    }


    /**
     * 답변 채택
     */
    @Transactional
    public AnswerResponse acceptAnswer(Long qId, Long aId) {
        Question findQuestion = getQuestionById(qId);

        String nickname = "tester"; // TODO user 정보 등록

        Answer findAnswer = getAnswerById(aId);
        findAnswer.changeStatus();
        findQuestion.changeStatus();

        return AnswerResponse.toResponse(
                findAnswer.getAnswererId(),
                nickname,
                findAnswer.getAnswer(),
                findAnswer.getCreatedAt(),
                findAnswer.getRecommendationCnt(),
                findAnswer.getTag(),
                findAnswer.getSource(),
                findAnswer.isAi(),
                findAnswer.isAccepted()
        );
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
