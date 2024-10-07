package com.hanshin.supernova.search.application;

import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.hashtag.application.HashtagService;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.search.dto.response.SearchResponse;
import com.hanshin.supernova.search.util.SearchSortType;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final QuestionRepository questionRepository;
    private final CommunityRepository communityRepository;
    private final HashtagService hashtagService;

    /*
     * 노트 검색 - 제목 또는 내용이 해당 키워드를 포함하고 있을 경우
     */
    @Transactional(readOnly = true)
    public Page<SearchResponse> searchByTitleOrContent(String searchKeyword, SearchSortType sortType, Pageable pageable) {
        Page<Question> questionPage;

        // 정렬
        questionPage = sortAndGetQuestionPage(searchKeyword, sortType, pageable);

        return questionPage.map(this::convertToSearchResponse);
    }

    /*
     * 노트 검색 - 해시태그가 해당 키워드를 포함하고 있을 경우
     */
    @Transactional(readOnly = true)
    public Page<SearchResponse> searchByHashtag(String hashtagName, SearchSortType sortType, Pageable pageable) {
        List<Question> questions = hashtagService.getQuestionsByHashtagName(hashtagName);

        // 검색 결과가 존재하지 않을 경우 예외처리 대신 빈 페이지 리스트를 반환한다.
        if(questions == null || questions.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 정렬
        sortQuestions(sortType, questions);

        // Question 을 SearchResponse 로 변환
        List<SearchResponse> searchResponses = questions.stream()
                .map(this::convertToSearchResponse)
                .collect(Collectors.toList());

        // 페이징 적용
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), searchResponses.size());
        List<SearchResponse> pageContent = searchResponses.subList(start, end);

        return new PageImpl<>(pageContent, pageable, searchResponses.size());
    }


    private Page<Question> sortAndGetQuestionPage(String searchKeyword, SearchSortType sortType,
            Pageable pageable) {
        return switch (sortType) {
            case ANSWER_CNT -> questionRepository.searchByKeywordOrderByAnswerCount(searchKeyword,
                    pageable);
            case VIEW_CNT -> questionRepository.searchByKeywordOrderByViewCount(searchKeyword,
                    pageable);
            case RECOMMENDATION_CNT -> questionRepository.searchByKeywordOrderByRecommendationCount(
                    searchKeyword, pageable);
            default -> questionRepository.searchByKeywordOrderByCreatedAt(searchKeyword,
                    pageable);
        };
    }

    private static void sortQuestions(SearchSortType sortType, List<Question> questions) {
        switch (sortType) {
            case ANSWER_CNT:
                questions.sort(Comparator.comparing(Question::getAnswerCnt).reversed());
                break;
            case VIEW_CNT:
                questions.sort(Comparator.comparing(Question::getViewCnt).reversed());
                break;
            case RECOMMENDATION_CNT:
                questions.sort(Comparator.comparing(Question::getRecommendationCnt).reversed());
                break;
            case CREATED_AT:
            default:
                questions.sort(Comparator.comparing(Question::getCreatedAt).reversed());
                break;
        }
    }

    private SearchResponse convertToSearchResponse(Question question) {
        return new SearchResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getAnswerCnt(),
                question.getViewCnt(),
                question.getRecommendationCnt(),
                getCommunityNameByIdOrThrowIfNotExist(question.getCommId()),
                question.getCreatedAt()
        );
    }

    private String getCommunityNameByIdOrThrowIfNotExist(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow(
                () -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR)
        );

        return community.getName();
    }

}