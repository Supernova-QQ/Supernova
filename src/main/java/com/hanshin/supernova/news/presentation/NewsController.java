package com.hanshin.supernova.news.presentation;

import com.hanshin.supernova.auth.v2.model.SecurityAuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.news.application.NewsService;
import com.hanshin.supernova.news.dto.request.NewsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    /**
     * 알림 생성: 관리자 또는 시스템만 생성 가능하다.
     */
    @PostMapping
    public ResponseEntity<?> createNews(
            @RequestBody @Valid NewsRequest request
    ) {
        var response = newsService.createNews(request);
        return ResponseDto.created(response);
    }

    /**
     * 알림 조회: 당사자만 조회 가능하다.
     */
    @GetMapping(path = "/{newsId}")
    public ResponseEntity<?> getNews(
            SecurityAuthUser user,
            @PathVariable(name = "newsId") Long newsId
    ) {
        var response = newsService.getNews(user, newsId);
        return ResponseDto.ok(response);
    }

    /**
     * 알림 수정: 관리자만 수정 가능하다.
     */
    @PutMapping(path = "/{newsId}")
    public ResponseEntity<?> updateNews(
            SecurityAuthUser user,
            @PathVariable(name = "newsId") Long newsId,
            @RequestBody @Valid NewsRequest request
    ) {
        var response = newsService.updateNews(user, newsId, request);
        return ResponseDto.ok(response);
    }

    /**
     * 알림 삭제: 관리자만 삭제 가능하다.
     */
    @DeleteMapping(path = "/{newsId}")
    public ResponseEntity<?> deleteNews(
            SecurityAuthUser user,
            @PathVariable(name = "newsId") Long newsId
    ) {
        var response = newsService.deleteNews(user, newsId);
        return ResponseDto.ok(response);
    }

    /**
     * 확인되지 않은 알림 목록
     */
    @GetMapping(path = "/unviewed-list")
    public ResponseEntity<?> getUnViewedNews(
            SecurityAuthUser user
    ) {
        var responses = newsService.getUnViewedNews(user);
        return ResponseDto.ok(responses);
    }

    /**
     * 확인된 알림 목록
     */
    @GetMapping(path = "/viewed-list")
    public ResponseEntity<?> getViewedNews(
            SecurityAuthUser user
    ) {
        var responses = newsService.getViewedNews(user);
        return ResponseDto.ok(responses);
    }

}
