package com.hanshin.supernova.news.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.news.application.NewsService;
import com.hanshin.supernova.news.dto.request.NewsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @Operation(summary = "알림 생성", description = "관리자 또는 시스템만 생성 가능하다.")
    @PostMapping
    public ResponseEntity<?> createNews(
            @Parameter(required = true, description = "알림 생성 요청")
            @RequestBody @Valid NewsRequest request
    ) {
        var response = newsService.createNews(request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "알림 조회", description = "당사자만 조회가 가능하다.")
    @GetMapping(path = "/{newsId}")
    public ResponseEntity<?> getNews(
            AuthUser user,
            @Parameter(description = "알림 고유 번호")
            @PathVariable(name = "newsId") Long newsId
    ) {
        var response = newsService.getNews(user, newsId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "알림 수정", description = "관리자만 수정 가능하다.")
    @PutMapping(path = "/{newsId}")
    public ResponseEntity<?> updateNews(
            AuthUser user,
            @Parameter(description = "알림 고유 번호")
            @PathVariable(name = "newsId") Long newsId,
            @Parameter(required = true, description = "알림 수정 요청")
            @RequestBody @Valid NewsRequest request
    ) {
        var response = newsService.updateNews(user, newsId, request);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "알림 삭제", description = "관리자만 삭제 가능하다.")
    @DeleteMapping(path = "/{newsId}")
    public ResponseEntity<?> deleteNews(
            AuthUser user,
            @Parameter(description = "알림 고유 번호")
            @PathVariable(name = "newsId") Long newsId
    ) {
        var response = newsService.deleteNews(user, newsId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "전체 알림 목록 조회", description = "당사자만 조회가 가능하다.")
    @GetMapping
    public ResponseEntity<?> getAllNews(
            AuthUser user,
            Pageable pageable
    ) {
        var responses = newsService.getAllNews(user, pageable);
        return ResponseDto.ok(responses);
    }

    @Operation(summary = "미확인 알림 목록 조회", description = "당사자만 조회가 가능하다.")
    @GetMapping(path = "/unviewed-list")
    public ResponseEntity<?> getUnViewedNews(
            AuthUser user
    ) {
        var responses = newsService.getUnViewedNews(user);
        return ResponseDto.ok(responses);
    }

    /**
     * 확인된 알림 목록
     */
    @Operation(summary = "확인된 알림 목록 조회", description = "당사자만 조회가 가능하다.")
    @GetMapping(path = "/viewed-list")
    public ResponseEntity<?> getViewedNews(
            AuthUser user
    ) {
        var responses = newsService.getViewedNews(user);
        return ResponseDto.ok(responses);
    }
}
