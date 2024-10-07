package com.hanshin.supernova.search.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.search.application.SearchService;
import com.hanshin.supernova.search.dto.response.SearchResponse;
import com.hanshin.supernova.search.util.SearchSortType;
import com.hanshin.supernova.search.util.SearchType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<?> searchKeyword(
            @RequestParam(name = "search-keyword", required = true) String searchKeyword,                   // 검색 키워드
            @RequestParam(name = "search-type", defaultValue = "TITLE_OR_CONTENT") SearchType searchType,   // 검색 분류 기준: 제목 및 내용 / 해시태그
            @RequestParam(name = "sort-type", defaultValue = "ANSWER_CNT") SearchSortType sortType,         // 검색 정렬 기준: 답변수 많은 순, 추천수 많은 순, 조회수 높은 순, 최신순
            @PageableDefault(size = 2) Pageable pageable) {
        Page<SearchResponse> response;

        // 제목 및 내용, 또는 해시태그 옵션으로 검색
        if (searchType.equals(SearchType.TITLE_OR_CONTENT)) {
            response = searchService.searchByTitleOrContent(searchKeyword, sortType, pageable);
        } else {
            response = searchService.searchByHashtag(searchKeyword, sortType, pageable);
        }
        return ResponseDto.ok(response);
    }
}
