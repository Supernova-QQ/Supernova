package com.hanshin.supernova.question.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.domain.CommunityMember;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionRecommendation;
import com.hanshin.supernova.question.domain.QuestionView;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.question.dto.response.QuestionResponse;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRecommendationRepository;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.question.infrastructure.QuestionViewRepository;
import com.hanshin.supernova.user.domain.User;
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
public class QuestionService extends AbstractValidateService {

    private final QuestionRepository questionRepository;
    private final QuestionViewRepository questionViewRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final QuestionRecommendationRepository questionRecommendationRepository;

    /**
     * 질문 등록
     */
    @Transactional
    public QuestionSaveResponse createQuestion(AuthUser user,
            QuestionRequest request) {
        Community findCommunity = getCommunityOrThrowIfNotExist(request.getCommId());
        User findUser = getUserOrThrowIfNotExist(user.getId());

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
    @Transactional
    public QuestionResponse getQuestion(AuthUser user, Long qId) {

        // 조회를 시도하는 회원의 중복 체크 및 조회수 증가

        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());
        Long viewer_id = findUser.getId();

        if (!questionViewRepository.existsByViewerIdAndQuestionId(viewer_id, qId)) {
            questionViewRepository.save(
                    QuestionView.builder()
                            .viewedAt(LocalDate.now())
                            .questionId(qId)
                            .viewerId(viewer_id)
                            .build());
            findQuestion.updateViewCnt();
        } else {
            questionViewRepository.findByViewerIdAndQuestionId(viewer_id, qId).updateViewedAt();
        }

        return getQuestionResponse(findQuestion);
    }

    /**
     * 질문 수정
     */
    @Transactional
    public QuestionSaveResponse editQuestion(AuthUser user, Long qId, QuestionRequest request) {

        Community findCommunity = getCommunityOrThrowIfNotExist(request.getCommId());

        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        validateSameQuestionerById(findQuestion, findUser.getId());

        findQuestion.updateQuestion(request.getTitle(), request.getContent(), request.getImgUrl(),
                findCommunity.getId());

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

        Question findQuestion = getQuestionById(qId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        log.info("questioner ID = {}", findQuestion.getQuestionerId());
        log.info("user ID = {}", findUser.getId());

        validateSameQuestionerById(findQuestion, findUser.getId());

        Community findCommunity = getCommunityOrThrowIfNotExist(
                findQuestion.getCommId());

        questionRepository.deleteById(qId);

        findCommunity.getCommCounter().decreaseQuestionCnt();

        return new SuccessResponse("성공적으로 삭제되었습니다.");
    }

    @Transactional
    public QuestionResponse updateQuestionRecommendation(AuthUser user, Long qId) {
        Question findQuestion = getQuestionById(qId);
        User findUser = getUserOrThrowIfNotExist(user.getId());
        // 자신의 질문은 추천하지 못하도록 예외처리
        if (findQuestion.getQuestionerId().equals(findUser.getId())) {
            throw new AuthInvalidException(ErrorType.WRITER_CANNOT_RECOMMEND_ERROR);
        }
        // 기존 추천한 이력 유무에 따른 추천수 증감
        QuestionRecommendation questionRecommendation = questionRecommendationRepository.findByQuestionIdAndRecommenderId(
                findQuestion.getId(), findUser.getId());
        if (questionRecommendation == null) {
            questionRecommendationRepository.save(QuestionRecommendation.builder()
                    .recommendedAt(LocalDate.now())
                    .questionId(findQuestion.getId())
                    .recommenderId(findUser.getId())
                    .build());

            findQuestion.increaseRecommendationCnt();
        } else {
            questionRecommendationRepository.deleteById(questionRecommendation.getId());
            findQuestion.decreaseRecommendationCnt();
        }

        return getQuestionResponse(findQuestion);
    }

    /**
     * 사용자가 등록된 커뮤니티 목록 제공
     */
    @Transactional(readOnly = true)
    public List<CommunityInfoResponse> getMyCommunities(AuthUser user) {
        User findUser = getUserOrThrowIfNotExist(user.getId());

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


    private static void validateSameQuestionerById(Question findQuestion, Long user_id) {
        if (!findQuestion.getQuestionerId().equals(user_id)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }

    private Question getQuestionById(Long q_Id) {
        return questionRepository.findById(q_Id).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }

    private QuestionResponse getQuestionResponse(Question findQuestion) {
        User findUser = getUserOrThrowIfNotExist(findQuestion.getQuestionerId());
        Community findCommunity = getCommunityOrThrowIfNotExist(findQuestion.getCommId());
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
