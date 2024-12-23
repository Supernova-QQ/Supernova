package com.hanshin.supernova.answer.application;

import com.hanshin.supernova.answer.domain.AiComment;
import com.hanshin.supernova.answer.dto.request.AiCommentRequest;
import com.hanshin.supernova.answer.dto.response.AiCommentResponse;
import com.hanshin.supernova.answer.infrastructure.AiCommentRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiCommentService extends AbstractValidateService {

    private final AiCommentRepository aiCommentRepository;
    private final UserRepository userRepository;

    // AI 댓글 등록
    @Transactional
    public void createAiComment(AiCommentRequest request) {
        getQuestionOrThrowIfNotExist(request.getQuestionId());
        buildAndSaveAiComment(request);
    }

    // AI 댓글 조회
    @Transactional(readOnly = true)
    public AiCommentResponse getAiComment(Long questionId) {
        Question findQuestion = getQuestionOrThrowIfNotExist(questionId);

        AiComment findAiComment = getAiCommentOrThrowIfNotExist(findQuestion.getId());

        User systemUser = getUserOrThrowIfNotExist(findAiComment.getUserId());
        return getAiCommentResponse(findAiComment, systemUser);
    }

    // AI 댓글 수정(regeneration)
    public AiCommentResponse updateAiComment(AuthUser user, AiCommentRequest request) {
        Question findQuestion = getQuestionOrThrowIfNotExist(request.getQuestionId());

        AiComment findAiComment = getAiCommentOrThrowIfNotExist(findQuestion.getId());

        verifySameUser(user.getId(), findQuestion.getQuestionerId());

        findAiComment.update(request.getAiAnswer());

        User systemUser = getUserOrThrowIfNotExist(findAiComment.getUserId());
        return getAiCommentResponse(findAiComment, systemUser);
    }


    private void buildAndSaveAiComment(AiCommentRequest request) {
        User systemUser = userRepository.findByAuthority(Authority.SYSTEM).orElseThrow(
                () -> new UserInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR)
        );

        AiComment aiComment = AiComment.builder()
                .aiComment(request.getAiAnswer())
                .questionId(request.getQuestionId())
                .userId(systemUser.getId())
                .build();

        aiCommentRepository.save(aiComment);
    }

    private static AiCommentResponse getAiCommentResponse(AiComment findAiComment,
            User systemUser) {
        return AiCommentResponse.toResponse(
                findAiComment.getId(),
                systemUser.getNickname(),
                findAiComment.getAiComment(),
                findAiComment.getCreatedAt()
        );
    }

    private AiComment getAiCommentOrThrowIfNotExist(Long questionId) {
        return aiCommentRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new AnswerInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR));
    }
}
