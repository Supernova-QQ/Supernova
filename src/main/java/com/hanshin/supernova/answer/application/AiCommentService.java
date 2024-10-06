package com.hanshin.supernova.answer.application;

import com.hanshin.supernova.answer.domain.AiComment;
import com.hanshin.supernova.answer.dto.request.AiCommentRequest;
import com.hanshin.supernova.answer.dto.response.AiCommentResponse;
import com.hanshin.supernova.answer.infrastructure.AiCommentRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiCommentService {

    private final AiCommentRepository aiCommentRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    // AI 댓글 등록
    @Transactional
    public void createAiComment(AiCommentRequest request) {
        getQuestionOrThrowIfNotExist(request.getQuestionId());
        buildAndSaveAiComment(request);
    }

    // AI 댓글 조회
    @Transactional(readOnly = true)
    public AiCommentResponse getAiComment(Long questionId) {
        getQuestionOrThrowIfNotExist(questionId);

        AiComment findAiComment = aiCommentRepository.findByQuestionId(questionId);
        if (findAiComment == null) {
            return null;
        }

        User systemUser = getUserOrThrowIfNotExist(findAiComment.getUserId());
        return getAiCommentResponse(findAiComment, systemUser);
    }

    // AI 댓글 수정(regeneration)
    public AiCommentResponse updateAiComment(AuthUser user, AiCommentRequest request) {
        Question findQuestion = getQuestionOrThrowIfNotExist(request.getQuestionId());

        AiComment findAiComment = aiCommentRepository.findByQuestionId(request.getQuestionId());

        verifySameUser(user, findQuestion.getQuestionerId());

        findAiComment.update(request.getAiAnswer());

        User systemUser = getUserOrThrowIfNotExist(findAiComment.getUserId());
        return getAiCommentResponse(findAiComment, systemUser);
    }

    private static void verifySameUser(AuthUser user, Long questionerId) {
        if (!questionerId.equals(user.getId())) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }

    private Question getQuestionOrThrowIfNotExist(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }

    private User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
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
}
