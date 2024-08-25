package com.hanshin.supernova.question.application;

import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.response.QuestionInfoResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import java.awt.print.Pageable;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionListService {

    private final QuestionRepository questionRepository;
    private final CommunityRepository communityRepository;

    /**
     * 답변이 채택되지 않은 질문 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getUnAnsweredQuestionsByDesc(Long cId) {
        isCommunityExistsById(cId);

        List<Question> findUnAnsweredQuestions = questionRepository.findAllByCommIdAndIsResolvedOrderByCreatedAtDesc(
                cId, false);

        return getQuestionInfoResponses(
                findUnAnsweredQuestions);
    }

    /**
     * 답변이 채택되지 않은 질문 N개 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getUnAnswered4QuestionsByDesc(Long cId, int n) {
        isCommunityExistsById(cId);

        List<Question> findUnAnsweredQuestions = questionRepository.findTopNByIsResolvedOrderByCreatedAtDesc(
                false,
                (Pageable) PageRequest.of(0, n));

        return getQuestionInfoResponses(
                findUnAnsweredQuestions);
    }

    /**
     * 답변이 채택되지 않은 질문 목록 - 오래된 순
     */
    public List<QuestionInfoResponse> getUnAnsweredQuestionsByAsc(Long cId) {
        isCommunityExistsById(cId);

        List<Question> findUnAnsweredQuestions = questionRepository.findAllByCommIdAndIsResolvedOrderByCreatedAtAsc(
                cId, false);

        return getQuestionInfoResponses(
                findUnAnsweredQuestions);
    }

    /**
     * 전체 질문 목록 - 최신 순
     */
    public List<QuestionInfoResponse> getAllQuestionsByDesc(Long cId) {
        isCommunityExistsById(cId);

        List<Question> findAllQuestions = questionRepository.findAllByCommIdOrderByCreatedAtDesc(
                cId);

        return getQuestionInfoResponses(
                findAllQuestions);
    }

    /**
     * 커뮤니티별 전체 질문 - 오래된 순
     */
    public List<QuestionInfoResponse> getAllQuestionsByAsc(Long cId) {
        isCommunityExistsById(cId);

        List<Question> findAllQuestions = questionRepository.findAllByCommIdOrderByCreatedAtAsc(
                cId);

        return getQuestionInfoResponses(
                findAllQuestions);
    }


    private void isCommunityExistsById(Long commId) {
        if (!communityRepository.existsById(commId)) {
            throw new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR);
        }
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
