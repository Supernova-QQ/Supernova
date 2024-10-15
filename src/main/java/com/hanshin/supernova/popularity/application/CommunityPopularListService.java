package com.hanshin.supernova.popularity.application;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.popularity.dto.response.PopularQuestionResponse;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommunityPopularListService {

    private final QuestionRepository questionRepository;

    public List<PopularQuestionResponse> getPopular4QuestionsByCommunity(
            Long communityId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<Object[]> results = questionRepository.findTopNPopularQuestionsByCommunityAndDate(
                communityId, startDate, endDate, 4);
        return getPopularQuestionResponses(results);
    }

    public List<PopularQuestionResponse> getPopularQuestionsByCommunity(
            Long communityId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<Object[]> results = questionRepository.findAllPopularQuestionsByCommunityAndDate(
                communityId, startDate, endDate);
        return getPopularQuestionResponses(results);
    }


    private List<PopularQuestionResponse> getPopularQuestionResponses(List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    Long questionId = (Long) result[0];
                    Long viewCount = ((Number) result[1]).longValue();
                    Question findQuestion = questionRepository.findById(questionId)
                            .orElseThrow(() -> new QuestionInvalidException(
                                    ErrorType.QUESTION_NOT_FOUND_ERROR));
                    return PopularQuestionResponse.builder()
                            .id(questionId)
                            .title(findQuestion.getTitle())
                            .content(findQuestion.getContent())
                            .viewCnt(viewCount)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
