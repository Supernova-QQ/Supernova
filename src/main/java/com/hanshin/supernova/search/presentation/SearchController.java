package com.hanshin.supernova.search.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.search.application.SearchService;
import com.hanshin.supernova.search.util.SearchSortType;
import lombok.RequiredArgsConstructor;
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
            @RequestParam(name = "search-keyword", required = true) String searchKeyword,
            @RequestParam(name = "sort-type", defaultValue = "ANSWER_CNT") SearchSortType sortType,
            @PageableDefault(size = 2) Pageable pageable) {
        var response = searchService.searchByTitleOrContent(searchKeyword, sortType, pageable);
        return ResponseDto.ok(response);
    }
}
