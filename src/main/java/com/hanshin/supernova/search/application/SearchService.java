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
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final QuestionRepository questionRepository;
    private final CommunityRepository communityRepository;

    /**
     * 노트 검색 - 키워드로 조회
     */
    @Transactional(readOnly = true)
    public Page<SearchResponse> searchByTitleOrContent(String searchKeyword, SearchSortType sortType, Pageable pageable) {

        // 제목 또는 내용이 해당 키워드를 포함하고 있을 경우
        Page<Question> questionPage;

        switch (sortType) {
            case ANSWER_CNT:
                questionPage = questionRepository.searchByKeywordOrderByAnswerCount(searchKeyword, pageable);
                break;
            case VIEW_CNT:
                questionPage = questionRepository.searchByKeywordOrderByViewCount(searchKeyword, pageable);
                break;
            case RECOMMENDATION_CNT:
                questionPage = questionRepository.searchByKeywordOrderByRecommendationCount(searchKeyword, pageable);
                break;
            case CREATED_AT:
            default:
                questionPage = questionRepository.searchByKeywordOrderByCreatedAt(searchKeyword, pageable);
                break;
        }

        return questionPage.map(this::convertToSearchResponse);
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