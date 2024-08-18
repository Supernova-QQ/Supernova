package com.hanshin.supernova.question.application;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionView;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.QuestionResponse;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.question.infrastructure.QuestionViewRepository;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionViewRepository questionViewRepository;

    /**
     * 질문 등록
     */
    @Transactional
    public QuestionSaveResponse createQuestion(Long cId, QuestionRequest request) {

        isTitleAndContentNeitherBlank(request);

        // TODO user 정보 받아오기

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .questionerId(1L)   // TODO user id
                .commId(1L)         // TODO community id
                .build();

        Question savedQuestion = questionRepository.save(question);

        // TODO hashtag save logic

        // TODO community save logic
//        communityService.registerQuestion()

        // TODO ContentWord save logic

        return QuestionSaveResponse.toResponse(savedQuestion.getId());
    }

    /**
     * 질문 조회
     */
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long qId) {

        // 조회를 시도하는 회원의 중복 체크 및 조회수 증가
        // TODO add user, add @Transactional
        Long viewer_id = 1L;
        if (!questionViewRepository.existsByViewerId(viewer_id)) {
            questionViewRepository.save(
                    QuestionView.builder()
                            .viewedAt(LocalDateTime.now())
                            .questionId(qId)
                            .viewerId(viewer_id)
                            .build());
            Question findQuestion = questionRepository.findById(qId).orElseThrow(
                    () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
            );
            findQuestion.updateViewCnt();
        } else {
            questionViewRepository.findByViewerId(viewer_id).updateViewedAt();
        }

        Question findQuestion = questionRepository.findById(qId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR));

        // TODO get hashtag
        List<String> hashtagNames = new LinkedList<>();

        return QuestionResponse.toResponse(
                findQuestion.getTitle(),
                findQuestion.getContent(),
                findQuestion.isResolved(),
                findQuestion.getCreatedAt(),
                findQuestion.getModifiedAt(),
                findQuestion.getViewCnt(),
                findQuestion.getRecommendationCnt(),
                hashtagNames);
    }

    /**
     * 질문 수정
     */
    @Transactional
    public QuestionSaveResponse editQuestion(Long c_id, Long qId, QuestionRequest request) {

        // TODO 작성자와 수정자 동일성 확인

        Question findQuestion = questionRepository.findById(qId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
        isTitleAndContentNeitherBlank(request);
        findQuestion.updateQuestion(request.getTitle(), request.getContent());

        // TODO hashtag update logic

        // TODO community update logic

        // TODO ContentWord update logic

        return QuestionSaveResponse.toResponse(findQuestion.getId());
    }


    /**
     * 질문 삭제
     */




    private static void isTitleAndContentNeitherBlank(QuestionRequest request) {
        if (request.getTitle().isBlank() || request.getContent().isBlank()) {
            throw new QuestionInvalidException(ErrorType.NEITHER_BLANK_ERROR);
        }
    }
}
