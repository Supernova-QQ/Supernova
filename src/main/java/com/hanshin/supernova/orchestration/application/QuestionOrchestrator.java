package com.hanshin.supernova.orchestration.application;

import com.hanshin.supernova.ai_comment.application.AiAnswerService;
import com.hanshin.supernova.answer.application.AiCommentService;
import com.hanshin.supernova.answer.domain.AiComment;
import com.hanshin.supernova.answer.dto.request.AiCommentRequest;
import com.hanshin.supernova.ai_comment.dto.response.AiAnswerResponse;
import com.hanshin.supernova.answer.dto.response.AiCommentResponse;
import com.hanshin.supernova.answer.infrastructure.AiCommentRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.hashtag.application.HashtagService;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
import com.hanshin.supernova.news.application.NewsService;
import com.hanshin.supernova.news.domain.Type;
import com.hanshin.supernova.news.dto.request.NewsRequest;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionOrchestrator extends AbstractValidateService {

    private final QuestionService questionService;
    private final HashtagService hashtagService;
    private final AiAnswerService aiAnswerService;
    private final AiCommentService aiCommentService;
    private final NewsService newsService;
    private final AiCommentRepository aiCommentRepository;

    /**
     * 질문 등록과 함께 연관 기능 수행
     **/
    @Transactional
    public QuestionSaveResponse createQuestionWithAiAnswer(AuthUser user, QuestionRequest request) {

        // 질문 등록
        QuestionSaveResponse questionSaveResponse = questionService.createQuestion(user, request);
        Question savedQuestion = getQuestionOrThrowIfNotExist(questionSaveResponse.getQuestionId());

        // 해시태그 등록
        HashtagRequest hashtagRequest = new HashtagRequest();
        hashtagRequest.setHashtagNames(request.getHashtags());
        hashtagService.saveQuestionHashtag(savedQuestion.getId(), hashtagRequest, user);

        // AI 답변이 요청되었을 경우 비동기적으로 생성
        if (request.isAiAnswerRequested()) {
            asynchronouslyCreateAiAnswerAndCommentAndNews(user, savedQuestion);
        }

        return questionSaveResponse;
    }

    @Transactional
    public QuestionSaveResponse updateQuestionWithHashtag(AuthUser user, Long questionId,
            QuestionRequest request) {

        // 질문 수정
        QuestionSaveResponse questionUpdateResponse = questionService.editQuestion(user, questionId,
                request);
        Question savedQuestion = getQuestionOrThrowIfNotExist(
                questionUpdateResponse.getQuestionId());

        // 해시태그 등록(수정)
        HashtagRequest hashtagRequest = new HashtagRequest();
        hashtagRequest.setHashtagNames(request.getHashtags());
        hashtagService.saveQuestionHashtag(savedQuestion.getId(), hashtagRequest, user);

        AiComment aiComment = aiCommentRepository.findByQuestionId(savedQuestion.getId())
                .orElse(null);

        // AI 답변이 요청되었을 경우 비동기적으로 생성
        if (request.isAiAnswerRequested()
                && aiComment == null) {           // 수정 이전에 AI 답변이 존재하지 않을 경우 새로 생성
            asynchronouslyCreateAiAnswerAndCommentAndNews(user, savedQuestion);
        } else if (request.isAiAnswerRequested()
                && aiComment != null) {    // 수정 이전에 이미 AI 답변이 존재할 경우 발전된 답변 요청
            asynchronouslyRegenerateAiAnswerAndCommentAndNews(user, savedQuestion);
        }

        return questionUpdateResponse;
    }

    /**
     * AI 답변 새로 받기
     **/
    @Transactional
    public AiCommentResponse updateAiAnswer(AuthUser user, Long questionId) {
        AiAnswerResponse regenerationResponse = getRegenerationResponse(
                user, questionId);

        // 답변 등록
        return aiCommentService.updateAiComment(
                user, buildAiCommentRequest(questionId, regenerationResponse.getAnswer()));
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void asynchronouslyCreateAiAnswerAndCommentAndNews(AuthUser user,
            Question savedQuestion) {
        CompletableFuture.runAsync(() -> {
            try {
                AiAnswerResponse aiAnswerResponse = aiAnswerService.generateAiAnswer(
                        user, savedQuestion.getTitle(), savedQuestion.getContent());

                aiCommentService.createAiComment(
                        buildAiCommentRequest(savedQuestion.getId(), aiAnswerResponse.getAnswer()));

                newsService.createNews(buildNewsRequest(savedQuestion));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void asynchronouslyRegenerateAiAnswerAndCommentAndNews(AuthUser user,
            Question savedQuestion) {
        CompletableFuture.runAsync(() -> {
            try {
                AiAnswerResponse regenerationResponse = getRegenerationResponse(user,
                        savedQuestion.getId()); // 발전된 답변 생성

                aiCommentService.updateAiComment(user,
                        buildAiCommentRequest(savedQuestion.getId(),
                                regenerationResponse.getAnswer()));    // 답변 수정

                newsService.createNews(buildNewsRequest(savedQuestion));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    private AiAnswerResponse getRegenerationResponse(AuthUser user, Long questionId) {
        Question findQuestion = getQuestionOrThrowIfNotExist(questionId);
        AiComment findAiComment = aiCommentRepository.findByQuestionId(findQuestion.getId())
                .orElse(null);

        // AI 답변 재신청
        return aiAnswerService.regenerateAiAnswer(
                user, findQuestion.getTitle(), findQuestion.getContent(),
                findAiComment.getAiComment());
    }

    private static AiCommentRequest buildAiCommentRequest(Long questionId,
            String answer) {
        return AiCommentRequest.builder()
                .questionId(questionId)
                .aiAnswer(answer)
                .build();
    }

    private static NewsRequest buildNewsRequest(Question question) {

        // 알림에 제목 포함시키기
        String title = question.getTitle();
        if (title.length() > 8) {
            title = title.substring(0, 8);
        }
        return NewsRequest.builder()
                .title("\"" + title + "...\"" + " 게시글에 인공지능 댓글이 등록되었습니다.")
                .content("지금 바로 확인해보세요!")
                .type(Type.ANSWER)
                .hasRelatedContent(true)
                .relatedContentId(question.getId())
                .receiverId(question.getQuestionerId())
                .build();
    }
}
