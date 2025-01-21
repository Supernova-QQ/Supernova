package com.hanshin.supernova.question.application;

import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.response.QuestionInfoResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.validation.CommunityValidator;
import com.hanshin.supernova.validation.UserValidator;
import java.util.LinkedList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionListService {

    private final CommunityValidator communityValidator;
    private final UserValidator userValidator;

    private final QuestionRepository questionRepository;

    public List<QuestionInfoResponse> getNLatestQuestionsByDesc(int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Question> findAllQuestions = questionRepository.findNLatestQuestionsByCreatedAtDesc(limit, pageable);

        return getQuestionInfoResponses(findAllQuestions);
    }

    public List<QuestionInfoResponse> getNLatestQuestionsFromGeneralCommunityByDesc(int limit) {

        Pageable pageable = PageRequest.of(0, limit);
        List<Question> findAllQuestions = questionRepository.findNLatestQuestionsFromGeneralCommunityByCreatedAtDesc(limit, pageable);

        return getQuestionInfoResponses(findAllQuestions);
    }

    /**
     * 답변이 채택되지 않은 질문 목록 - 최신 순
     */
    public Page<QuestionInfoResponse> getUnAnsweredQuestionsByDesc(Long cId, Pageable pageable) {
        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(cId);

        Page<Question> findUnAnsweredQuestions = questionRepository.findAllByCommIdAndIsResolvedOrderByCreatedAtDesc(
                findCommunity.getId(), false, pageable);

        return findUnAnsweredQuestions.map(this::convertToQuestionInfoResponse);
    }

    /**
     * 답변이 채택되지 않은 질문 N개 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getUnAnswered4QuestionsByDesc(Long cId, int n) {
        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(cId);

        Pageable pageable = PageRequest.of(0, n);
        List<Question> findUnAnsweredQuestions = questionRepository.findByIsResolvedOrderByCreatedAtDesc(findCommunity.getId(),false, pageable);

        return getQuestionInfoResponses(findUnAnsweredQuestions);

    }

    /**
     * 답변이 채택되지 않은 질문 목록 - 오래된 순
     */
    public Page<QuestionInfoResponse> getUnAnsweredQuestionsByAsc(Long cId, Pageable pageable) {
        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(cId);

        Page<Question> findUnAnsweredQuestions = questionRepository.findAllByCommIdAndIsResolvedOrderByCreatedAtAsc(
                findCommunity.getId(), false, pageable);

        return findUnAnsweredQuestions.map(this::convertToQuestionInfoResponse);
    }

    /**
     * 커뮤니티별 전체 질문 - 최신 순
     */
    public Page<QuestionInfoResponse> getAllQuestionsByDesc(Long cId, Pageable pageable) {
        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(cId);

        Page<Question> findAllQuestions = questionRepository.findAllByCommIdOrderByCreatedAtDesc(
                findCommunity.getId(), pageable);

        return findAllQuestions.map(this::convertToQuestionInfoResponse);
    }

    /**
     * 커뮤니티별 전체 질문 - 오래된 순
     */
    public Page<QuestionInfoResponse> getAllQuestionsByAsc(Long cId, Pageable pageable) {
        Community findCommunity = communityValidator.getCommunityOrThrowIfNotExist(cId);

        Page<Question> findAllQuestions = questionRepository.findAllByCommIdOrderByCreatedAtAsc(
                findCommunity.getId(), pageable);

        return findAllQuestions.map(this::convertToQuestionInfoResponse);
    }


    private QuestionInfoResponse convertToQuestionInfoResponse(Question question) {
        User findUser = userValidator.getUserOrThrowIfNotExist(question.getQuestionerId());
        return new QuestionInfoResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getCreatedAt(),
                findUser.getNickname()
        );
    }

    private static List<QuestionInfoResponse> getQuestionInfoResponses(
            List<Question> findUnAnsweredQuestions) {
        List<QuestionInfoResponse> questionInfoResponses = new LinkedList<>();
        findUnAnsweredQuestions.forEach(question -> {
            questionInfoResponses.add(QuestionInfoResponse.toResponse(
                    question.getId(),
                    question.getTitle(),
                    question.getContent(),
                    question.getCreatedAt(),
                    question.getQuestionerId().toString()
            ));
        });
        return questionInfoResponses;
    }
}
