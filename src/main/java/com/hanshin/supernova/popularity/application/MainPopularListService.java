package com.hanshin.supernova.popularity.application;

import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.hashtag.HashtagInvalidException;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.hashtag.domain.Hashtag;
import com.hanshin.supernova.hashtag.infrastructure.HashtagRepository;
import com.hanshin.supernova.popularity.dto.response.PopularCommunityResponse;
import com.hanshin.supernova.popularity.dto.response.PopularHashtagResponse;
import com.hanshin.supernova.popularity.dto.response.PopularQuestionResponse;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.question.infrastructure.QuestionViewRepository;
import com.hanshin.supernova.redis.community_stat.infrastructure.CommunityStatsRepository;
import com.hanshin.supernova.redis.hashtag_stat.infrastructure.HashtagStatsRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MainPopularListService {

    private final CommunityRepository communityRepository;
    private final CommunityStatsRepository communityStatsRepository;
    private final QuestionViewRepository questionViewRepository;
    private final QuestionRepository questionRepository;
    private final HashtagStatsRepository hashtagStatsRepository;
    private final HashtagRepository hashtagRepository;

    public List<PopularCommunityResponse> getTop3Communities() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<Object[]> results = communityStatsRepository.findTop3CommunitiesByVisitorsInDateRange(
                startDate, endDate);
        return getPopularCommunityResponses(results);
    }

    public List<PopularQuestionResponse> getTop5Questions() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<Object[]> results = questionViewRepository.findTopNQuestionsByViewCntInDateRange(
                startDate, endDate, 5);
        return getPopularQuestionResponses(results);
    }

    public List<PopularHashtagResponse> getTop10Hashtags() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Object[]> results = hashtagStatsRepository.findTop10HashtagsByTaggersInDateRange(
                yesterday);
        return getPopularHashtagResponses(results);
    }

    public PopularQuestionResponse getMostViewedQuestion() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(1);
        List<Object[]> results = questionViewRepository.findTopNQuestionsByViewCntInDateRange(
                startDate, endDate, 1);
        return getPopularQuestionResponses(results).get(0);
    }

//    public PopularAnswerResponse getMostLikedAnswer() {}


    private List<PopularCommunityResponse> getPopularCommunityResponses(
            List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    Long communityId = (Long) result[0];
                    Long visitorCnt = (Long) result[1];
                    String communityName = communityRepository.findById(communityId)
                            .map(Community::getName)
                            .orElse("Unknown Community");
                    return PopularCommunityResponse.builder()
                            .id(communityId)
                            .name(communityName)
                            .visitorCnt(visitorCnt)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<PopularQuestionResponse> getPopularQuestionResponses(
            List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    Long questionId = (Long) result[0];
                    Long viewCnt = (Long) result[1];
                    Question findQuestion = questionRepository.findById(questionId)
                            .orElseThrow(() -> new QuestionInvalidException(
                                    ErrorType.QUESTION_NOT_FOUND_ERROR));
                    return PopularQuestionResponse.builder()
                            .id(questionId)
                            .title(findQuestion.getTitle())
                            .content(findQuestion.getContent())
                            .viewCnt(viewCnt)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<PopularHashtagResponse> getPopularHashtagResponses(
            List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    Long hashtagId = (Long) result[0];
                    Long tagCnt = (Long) result[1];
                    Hashtag findHashtag = hashtagRepository.findById(hashtagId).orElseThrow(
                            () -> new HashtagInvalidException(ErrorType.HASHTAG_NOT_FOUND_ERROR)
                    );
                    return PopularHashtagResponse.builder()
                            .name(findHashtag.getName())
                            .tagCnt(tagCnt)
                            .build();
                })
                .collect(Collectors.toList());
    }

//    public List<PopularCommunityResponse> getPopularCommunities() {}
}
