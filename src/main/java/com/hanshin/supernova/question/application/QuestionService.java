package com.hanshin.supernova.question.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.community.domain.CommunityMember;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionView;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.question.dto.response.QuestionResponse;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.question.infrastructure.QuestionViewRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionViewRepository questionViewRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;

    /**
     * 질문 등록
     */
    @Transactional
    public QuestionSaveResponse createQuestion(AuthUser user, QuestionRequest request) {
        isCommunityExistsById(request.getCommId());

        User findUser = getUserOrThrowIfNotExist(user.getId());

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .questionerId(findUser.getId())
                .commId(request.getCommId())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // TODO ContentWord save logic

        return QuestionSaveResponse.toResponse(savedQuestion.getId());
    }

    /**
     * 질문 조회
     */
    @Transactional
    public QuestionResponse getQuestion(AuthUser user, Long qId) {

        // 조회를 시도하는 회원의 중복 체크 및 조회수 증가

        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());
        Long viewer_id = findUser.getId();

        if (!questionViewRepository.existsByViewerId(viewer_id)) {
            questionViewRepository.save(
                    QuestionView.builder()
                            .viewedAt(LocalDateTime.now())
                            .questionId(qId)
                            .viewerId(viewer_id)
                            .build());
            findQuestion.updateViewCnt();
        } else {
            questionViewRepository.findByViewerId(viewer_id).updateViewedAt();
        }

        return QuestionResponse.toResponse(
                findQuestion.getTitle(),
                findQuestion.getContent(),
                findQuestion.isResolved(),
                findQuestion.getCreatedAt(),
                findQuestion.getModifiedAt(),
                findQuestion.getViewCnt(),
                findQuestion.getRecommendationCnt(),
                findQuestion.getCommId());
    }

    /**
     * 질문 수정
     */
    @Transactional
    public QuestionSaveResponse editQuestion(AuthUser user, Long qId, QuestionRequest request) {
        isCommunityExistsById(request.getCommId());

        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        validateSameUserById(findQuestion, findUser.getId());

        findQuestion.updateQuestion(request.getTitle(), request.getContent(), request.getCommId());

        // TODO ContentWord update logic

        return QuestionSaveResponse.toResponse(findQuestion.getId());
    }

    /**
     * 질문 삭제
     */
    @Transactional
    public SuccessResponse deleteQuestion(AuthUser user, Long qId) {

        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        validateSameUserById(findQuestion, findUser.getId());

        questionRepository.deleteById(qId);

        return new SuccessResponse("성공적으로 삭제되었습니다.");
    }

    /**
     * 사용자가 등록된 커뮤니티 목록 제공
     */
    @Transactional(readOnly = true)
    public List<CommunityInfoResponse> getMyCommunities(AuthUser user, Long qId) {
        User findUser = getUserOrThrowIfNotExist(user.getId());

        List<CommunityInfoResponse> communityInfoResponses = new ArrayList<>();

        List<CommunityMember> findCMembers = communityMemberRepository.findAllByUserId(findUser.getId());
        findCMembers.forEach(findMember -> {
            communityInfoResponses.add(CommunityInfoResponse.toResponse(
                    findMember.getCommunityId(),
                    findMember.getCommunityName()
            ));
        });

        return communityInfoResponses;
    }


    private User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
    }

    private void isCommunityExistsById(Long commId) {
        if (!communityRepository.existsById(commId)) {
            throw new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR);
        }
    }

    private static void validateSameUserById(Question findQuestion, Long user_id) {
        if (!findQuestion.getQuestionerId().equals(user_id)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }

    private Question getQuestionById(Long q_Id) {
        return questionRepository.findById(q_Id).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }

}
