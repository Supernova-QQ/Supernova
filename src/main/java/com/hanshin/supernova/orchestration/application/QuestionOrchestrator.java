package com.hanshin.supernova.orchestration.application;

import com.hanshin.supernova.ai_comment.application.AiAnswerService;
import com.hanshin.supernova.answer.application.AiCommentService;
import com.hanshin.supernova.answer.domain.AiComment;
import com.hanshin.supernova.answer.dto.request.AiCommentRequest;
import com.hanshin.supernova.ai_comment.dto.response.AiAnswerResponse;
import com.hanshin.supernova.answer.dto.response.AiCommentResponse;
import com.hanshin.supernova.answer.infrastructure.AiCommentRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.exception.rate_limit.RateLimitExceededException;
import com.hanshin.supernova.hashtag.application.HashtagService;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
import com.hanshin.supernova.news.application.NewsService;
import com.hanshin.supernova.news.domain.Type;
import com.hanshin.supernova.news.dto.request.NewsRequest;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionOrchestrator {

    private final QuestionService questionService;
    private final HashtagService hashtagService;
    private final QuestionRepository questionRepository;
    private final AiAnswerService aiAnswerService;
    private final AiCommentService aiCommentService;
    private final NewsService newsService;
    private final AiCommentRepository aiCommentRepository;

    /*
    질문 등록과 함께 연관 기능 수행
     */
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
    public QuestionSaveResponse updateQuestionWithHashtag(AuthUser user, Long questionId, QuestionRequest request) {

        // 질문 등록
        QuestionSaveResponse questionUpdateResponse = questionService.editQuestion(user, questionId, request);
        Question savedQuestion = getQuestionOrThrowIfNotExist(questionUpdateResponse.getQuestionId());

        // 해시태그 등록
        HashtagRequest hashtagRequest = new HashtagRequest();
        hashtagRequest.setHashtagNames(request.getHashtags());
        hashtagService.saveQuestionHashtag(savedQuestion.getId(), hashtagRequest, user);

        // AI 답변이 요청되었을 경우 비동기적으로 생성
        if (request.isAiAnswerRequested()) {
            asynchronouslyCreateAiAnswerAndCommentAndNews(user, savedQuestion);
        }

        return questionUpdateResponse;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void asynchronouslyCreateAiAnswerAndCommentAndNews(AuthUser user, Question savedQuestion) {
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

    /*
    새로운 AI 댓글
     */
    @Transactional
    public AiCommentResponse updateAiAnswer(AuthUser user, Long questionId) {
        Question findQuestion = getQuestionOrThrowIfNotExist(questionId);
        AiComment findAiComment = aiCommentRepository.findByQuestionId(findQuestion.getId())
                .orElse(null);

        // AI 답변 재신청
        AiAnswerResponse regenerationResponse = aiAnswerService.regenerateAiAnswer(
                user, findQuestion.getTitle(), findQuestion.getContent(),
                findAiComment.getAiComment());

        // 답변 등록
        return aiCommentService.updateAiComment(
                user, buildAiCommentRequest(questionId, regenerationResponse.getAnswer()));
    }

    private Question getQuestionOrThrowIfNotExist(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
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
