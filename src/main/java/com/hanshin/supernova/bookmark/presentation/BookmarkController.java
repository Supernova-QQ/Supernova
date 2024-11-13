package com.hanshin.supernova.bookmark.presentation;

import com.hanshin.supernova.bookmark.application.BookmarkService;
import com.hanshin.supernova.bookmark.domain.Bookmark;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 질문 북마크 추가
     */
    @PostMapping("/questions/{questionId}")
    public ResponseEntity<Void> addQuestionBookmark(
            HttpServletRequest request,
            @PathVariable Long questionId) {
        bookmarkService.addQuestionBookmark(request, questionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 질문 북마크 삭제
     */
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> removeQuestionBookmark(
            HttpServletRequest request,
            @PathVariable Long questionId) {
        bookmarkService.removeQuestionBookmark(request, questionId);
        return ResponseEntity.ok().build();
    }

    /**
     * 답변 북마크 추가
     */
    @PostMapping("/answers/{answerId}")
    public ResponseEntity<Void> addAnswerBookmark(
            HttpServletRequest request,
            @PathVariable Long answerId) {
        bookmarkService.addAnswerBookmark(request, answerId);
        return ResponseEntity.ok().build();
    }

    /**
     * 답변 북마크 삭제
     */
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> removeAnswerBookmark(
            HttpServletRequest request,
            @PathVariable Long answerId) {
        bookmarkService.removeAnswerBookmark(request, answerId);
        return ResponseEntity.ok().build();
    }

    /**
     * 북마크된 질문 목록 조회
     */
    @GetMapping("/questions")
    public ResponseEntity<List<Bookmark>> getBookmarkedQuestions(HttpServletRequest request) {
        List<Bookmark> bookmarks = bookmarkService.getBookmarkedQuestions(request);
        return ResponseEntity.ok(bookmarks);
    }

    /**
     * 북마크된 답변 목록 조회
     */
    @GetMapping("/answers")
    public ResponseEntity<List<Bookmark>> getBookmarkedAnswers(HttpServletRequest request) {
        List<Bookmark> bookmarks = bookmarkService.getBookmarkedAnswers(request);
        return ResponseEntity.ok(bookmarks);
    }
}
