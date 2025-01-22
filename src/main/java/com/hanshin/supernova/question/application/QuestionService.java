package com.hanshin.supernova.question.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.domain.CommunityMember;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionRecommendation;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.question.dto.response.QuestionResponse;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRecommendationRepository;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.validation.AuthenticationUtils;
import com.hanshin.supernova.validation.CommunityValidator;
import com.hanshin.supernova.validation.QuestionValidator;
import com.hanshin.supernova.validation.UserValidator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionValidator questionValidator;
    private final CommunityValidator communityValidator;
    private final UserValidator userValidator;

    private final QuestionRepository questionRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final QuestionRecommendationRepository questionRecommendationRepository;

    /**
     * 질문 등록
     */
    @Transactional
    public QuestionSaveResponse createQuestion(AuthUser user, QuestionRequest request) {

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(request.getCommId());

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .imgUrl(request.getImgUrl())
                .questionerId(findUser.getId())
                .commId(findCommunity.getId())
                .build();

        Question savedQuestion = questionRepository.save(question);

        // 커뮤니티에 등록된 질문 개수 증가
        findCommunity.getCommCounter().increaseQuestionCnt();

        // TODO ContentWord save logic

        return QuestionSaveResponse.toResponse(
                savedQuestion.getId(),
                savedQuestion.getTitle(),
                savedQuestion.getContent(),
                savedQuestion.getCommId());
    }

    /**
     * 질문 조회
     */
    @Transactional(readOnly = true) //
    public QuestionResponse getQuestion(Long qId) {

        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        return getQuestionResponse(findQuestion);
    }

    /**
     * 질문 수정
     */
    @Transactional
    public QuestionSaveResponse editQuestion(AuthUser user, Long qId, QuestionRequest request) {

        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        AuthenticationUtils.verifySameUser(findUser.getId(), findQuestion.getQuestionerId());

        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(request.getCommId());

        Community originalCommunity = communityValidator.getCommunityOrThrowIfNotExist(findQuestion.getCommId());

        findQuestion.updateQuestion(request.getTitle(), request.getContent(), request.getImgUrl(),
                findCommunity.getId());

        // 질문 게시판 이동 시 각 커뮤니티에서 질문 수 증감
        if (!originalCommunity.getId().equals(request.getCommId())) {
            originalCommunity.getCommCounter().decreaseQuestionCnt();
            findCommunity.getCommCounter().increaseQuestionCnt();
        }

        // TODO ContentWord update logic

        return QuestionSaveResponse.toResponse(
                findQuestion.getId(),
                findQuestion.getTitle(),
                findQuestion.getContent(),
                findQuestion.getCommId());
    }

    /**
     * 질문 삭제
     */
    @Transactional
    public SuccessResponse deleteQuestion(AuthUser user, Long qId) {

        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        AuthenticationUtils.verifySameUser(findUser.getId(), findQuestion.getQuestionerId());

        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(
                findQuestion.getCommId());

        questionRepository.deleteById(qId);

        findCommunity.getCommCounter().decreaseQuestionCnt();

        return new SuccessResponse("성공적으로 삭제되었습니다.");
    }

    @Transactional
    public QuestionResponse updateQuestionRecommendation(AuthUser user, Long qId) {

        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        Question findQuestion = questionValidator.getQuestionOrThrowIfNotExist(qId);

        // 자신의 질문은 추천하지 못하도록 예외처리
        if (findQuestion.getQuestionerId().equals(findUser.getId())) {
            throw new AuthInvalidException(ErrorType.WRITER_CANNOT_RECOMMEND_ERROR);
        }

        // 기존 추천한 이력 유무에 따른 추천수 증감
        Optional<QuestionRecommendation> questionRecommendation = questionRecommendationRepository.findByQuestionIdAndRecommenderId(
                findQuestion.getId(), findUser.getId());
        if (questionRecommendation.isEmpty()) {
            questionRecommendationRepository.save(QuestionRecommendation.builder()
                    .recommendedAt(LocalDate.now())
                    .questionId(findQuestion.getId())
                    .recommenderId(findUser.getId())
                    .build());

            findQuestion.increaseRecommendationCnt();
        } else {
            questionRecommendationRepository.deleteById(questionRecommendation.get().getId());
            findQuestion.decreaseRecommendationCnt();
        }

        return getQuestionResponse(findQuestion);
    }

    /**
     * 사용자가 등록된 커뮤니티 목록 제공
     */
    @Transactional(readOnly = true)
    public List<CommunityInfoResponse> getMyCommunities(AuthUser user) {
        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        List<CommunityInfoResponse> communityInfoResponses = new ArrayList<>();

        List<CommunityMember> findUserCommunities = communityMemberRepository.findAllByUserId(
                findUser.getId());
        findUserCommunities.forEach(userCommunity -> {
            communityInfoResponses.add(CommunityInfoResponse.toResponse(
                    userCommunity.getCommunityId(),
                    userCommunity.getCommunityName()
            ));
        });

        return communityInfoResponses;
    }


    private QuestionResponse getQuestionResponse(Question findQuestion) {
        User findUser = userValidator.getUserOrThrowIfNotExist(findQuestion.getQuestionerId());
        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(findQuestion.getCommId());
        return QuestionResponse.toResponse(
                findQuestion.getTitle(),
                findQuestion.getContent(),
                findQuestion.getImgUrl(),
                findQuestion.isResolved(),
                findQuestion.getCreatedAt(),
                findQuestion.getModifiedAt(),
                findQuestion.getViewCnt(),
                findQuestion.getRecommendationCnt(),
                findQuestion.getCommId(),
                findQuestion.getQuestionerId(),
                findCommunity.getName(),
                findUser.getNickname());
    }

    // questionId를 통해 communityId 조회
    public Long findCommunityIdByQuestionId(Long questionId) {
        return questionRepository.findCommunityIdByQuestionId(questionId);
    }
}
