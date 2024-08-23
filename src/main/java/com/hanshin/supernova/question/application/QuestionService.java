package com.hanshin.supernova.question.application;

import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.community.domain.CommunityMember;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionView;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.question.dto.response.QuestionResponse;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.question.infrastructure.QuestionViewRepository;
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

    /**
     * 질문 등록
     */
    @Transactional
    public QuestionSaveResponse createQuestion(QuestionRequest request) {
        isCommunityExistsById(request.getCommId());

        Long user_id = 1L;  // TODO user 정보 받아오기
        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .questionerId(user_id)
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
    public QuestionResponse getQuestion(Long qId) {

        // 조회를 시도하는 회원의 중복 체크 및 조회수 증가
        // TODO add user, add @Transactional

        Question findQuestion = getQuestionById(qId);

        Long viewer_id = 1L;
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
    public QuestionSaveResponse editQuestion(Long qId, QuestionRequest request) {
        isCommunityExistsById(request.getCommId());

        Question findQuestion = getQuestionById(qId);

        Long user_id = 1L;  // TODO user 정보 받아오기
        validateSameUserById(findQuestion, user_id);

        findQuestion.updateQuestion(request.getTitle(), request.getContent(), request.getCommId());

        // TODO ContentWord update logic

        return QuestionSaveResponse.toResponse(findQuestion.getId());
    }

    /**
     * 질문 삭제
     */
    @Transactional
    public SuccessResponse deleteQuestion(Long qId) {

        Question findQuestion = getQuestionById(qId);

        Long user_id = 1L;  // TODO user 정보 받아오기
        validateSameUserById(findQuestion, user_id);

        questionRepository.deleteById(qId);

        return new SuccessResponse("성공적으로 삭제되었습니다.");
    }

    /**
     * 사용자가 등록된 커뮤니티 목록 제공
     */
    @Transactional(readOnly = true)
    public List<CommunityInfoResponse> getMyCommunities(Long qId) {
        Long user_id = 1L;  // TODO user 정보 받아오기
        List<CommunityInfoResponse> communityInfoResponses = new ArrayList<>();

        List<CommunityMember> findCMembers = communityMemberRepository.findAllByUserId(user_id);
        findCMembers.forEach(findMember -> {
            communityInfoResponses.add(CommunityInfoResponse.toResponse(
                    findMember.getCommunityId(),
                    findMember.getCommunityName()
            ));
        });

        return communityInfoResponses;
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
