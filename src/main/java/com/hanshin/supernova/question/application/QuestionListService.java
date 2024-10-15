package com.hanshin.supernova.question.application;

import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.response.QuestionInfoResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import org.springframework.data.domain.Pageable;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionListService extends AbstractValidateService {

    private final QuestionRepository questionRepository;

    /**
     * 답변이 채택되지 않은 질문 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getUnAnsweredQuestionsByDesc(Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        List<Question> findUnAnsweredQuestions = questionRepository.findAllByCommIdAndIsResolvedOrderByCreatedAtDesc(
                findCommunity.getId(), false);

        return getQuestionInfoResponses(
                findUnAnsweredQuestions);
    }

    /**
     * 답변이 채택되지 않은 질문 N개 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getUnAnswered4QuestionsByDesc(Long cId, int n) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        Pageable pageable = PageRequest.of(0, n);
        List<Question> findUnAnsweredQuestions = questionRepository.findByIsResolvedOrderByCreatedAtDesc(false, pageable);

        return getQuestionInfoResponses(findUnAnsweredQuestions);

    }

    /**
     * 답변이 채택되지 않은 질문 목록 - 오래된 순
     */
    public List<QuestionInfoResponse> getUnAnsweredQuestionsByAsc(Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        List<Question> findUnAnsweredQuestions = questionRepository.findAllByCommIdAndIsResolvedOrderByCreatedAtAsc(
                findCommunity.getId(), false);

        return getQuestionInfoResponses(
                findUnAnsweredQuestions);
    }

    /**
     * 전체 질문 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getAllQuestionsByDesc(Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        List<Question> findAllQuestions = questionRepository.findAllByCommIdOrderByCreatedAtDesc(
                findCommunity.getId());

        return getQuestionInfoResponses(
                findAllQuestions);
    }

    /**
     * 커뮤니티별 전체 질문 - 오래된 순
     */
    public List<QuestionInfoResponse> getAllQuestionsByAsc(Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        List<Question> findAllQuestions = questionRepository.findAllByCommIdOrderByCreatedAtAsc(
                findCommunity.getId());

        return getQuestionInfoResponses(
                findAllQuestions);
    }


    private static List<QuestionInfoResponse> getQuestionInfoResponses(
            List<Question> findUnAnsweredQuestions) {
        List<QuestionInfoResponse> questionInfoResponses = new LinkedList<>();
        findUnAnsweredQuestions.forEach(question -> {
            questionInfoResponses.add(QuestionInfoResponse.toResponse(
                    question.getId(),
                    question.getTitle(),
                    question.getContent()
            ));
        });
        return questionInfoResponses;
    }
}
