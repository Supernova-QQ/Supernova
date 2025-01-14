//package com.hanshin.supernova.bookmark.presentation;
//
//import com.hanshin.supernova.bookmark.application.BookmarkService;
//import com.hanshin.supernova.bookmark.domain.Bookmark;
//import io.jsonwebtoken.Claims;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/bookmarks")
//public class BookmarkController {
//
//    private final BookmarkService bookmarkService;
//
//    /**
//     * 질문 북마크 추가
//     */
//    @PostMapping("/questions/{questionId}")
//    public ResponseEntity<Void> addQuestionBookmark(
//            HttpServletRequest request,
//            @PathVariable Long questionId) {
//        bookmarkService.addQuestionBookmark(request, questionId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 질문 북마크 삭제
//     */
//    @DeleteMapping("/questions/{questionId}")
//    public ResponseEntity<Void> removeQuestionBookmark(
//            HttpServletRequest request,
//            @PathVariable Long questionId) {
//        bookmarkService.removeQuestionBookmark(request, questionId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 답변 북마크 추가
//     */
//    @PostMapping("/answers/{answerId}")
//    public ResponseEntity<Void> addAnswerBookmark(
//            HttpServletRequest request,
//            @PathVariable Long answerId) {
//        bookmarkService.addAnswerBookmark(request, answerId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 답변 북마크 삭제
//     */
//    @DeleteMapping("/answers/{answerId}")
//    public ResponseEntity<Void> removeAnswerBookmark(
//            HttpServletRequest request,
//            @PathVariable Long answerId) {
//        bookmarkService.removeAnswerBookmark(request, answerId);
//        return ResponseEntity.ok().build();
//    }
//
//    /**
//     * 북마크된 질문 목록 조회
//     */
//    @GetMapping("/questions")
//    public ResponseEntity<List<Bookmark>> getBookmarkedQuestions(HttpServletRequest request) {
//        List<Bookmark> bookmarks = bookmarkService.getBookmarkedQuestions(request);
//        return ResponseEntity.ok(bookmarks);
//    }
//
//    /**
//     * 북마크된 답변 목록 조회
//     */
//    @GetMapping("/answers")
//    public ResponseEntity<List<Bookmark>> getBookmarkedAnswers(HttpServletRequest request) {
//        List<Bookmark> bookmarks = bookmarkService.getBookmarkedAnswers(request);
//        return ResponseEntity.ok(bookmarks);
//    }
//}
package com.hanshin.supernova.bookmark.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.bookmark.application.BookmarkService;
import com.hanshin.supernova.bookmark.domain.Bookmark;
import com.hanshin.supernova.bookmark.dto.request.BookmarkRequest;
import com.hanshin.supernova.bookmark.dto.response.BookmarkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/communities/{c_id}/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Operation(summary = "로그인한 유저의 질문 북마크 리스트 조회")
    @GetMapping("/questions")
    public List<BookmarkResponse> getBookmarkedQuestions(
            AuthUser authUser,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long commId) {
        List<BookmarkResponse> responses = bookmarkService.getBookmarkedQuestions(authUser.getId(), commId);

        // 로그 추가
        log.info("User ID: {}, Community ID: {} - Retrieved Bookmarked Questions: {}",
                authUser.getId(), commId, responses);

        return responses;
    }

    @Operation(summary = "로그인한 유저의 답변 북마크 리스트 조회")
    @GetMapping("/answers")
    public List<BookmarkResponse> getBookmarkedAnswers(
            AuthUser authUser,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long commId) {
        List<BookmarkResponse> responses = bookmarkService.getBookmarkedAnswers(authUser.getId(), commId);

        // 로그 추가
        log.info("User ID: {}, Community ID: {} - Retrieved Bookmarked Answers: {}",
                authUser.getId(), commId, responses);

        return responses;
    }

    @Operation(summary = "북마크 추가")
    @PostMapping
    public void addBookmark(
            AuthUser authUser,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long c_id,
            @Parameter(required = true, description = "북마크 추가 요청")
            @RequestBody BookmarkRequest request) {
        request.setCommId(c_id); // 요청 바디에 커뮤니티 ID 설정
        bookmarkService.addBookmark(authUser.getId(), request);
    }

    @Operation(summary = "북마크 삭제")
    @DeleteMapping
    public void removeBookmark(
            AuthUser authUser,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long c_id,
            @Parameter(required = true, description = "북마크 삭제 요청")
            @RequestBody BookmarkRequest request) {
        request.setCommId(c_id); // 요청 바디에 커뮤니티 ID 설정
        bookmarkService.removeBookmark(authUser.getId(), request);
    }

    @Operation(summary = "특정 질문 북마크 상태 조회")
    @GetMapping("/questions/{questionId}")
    public boolean isQuestionBookmarked(
            AuthUser authUser,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long commId,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable(name = "questionId") Long questionId) {
        return bookmarkService.isQuestionBookmarked(authUser.getId(), commId, questionId);
    }

    @Operation(summary = "특정 답변 북마크 상태 조회")
    @GetMapping("/answers/{answerId}")
    public boolean isAnswerBookmarked(
            AuthUser authUser,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long commId,
            @Parameter(description = "답변자 고유 번호")
            @PathVariable(name = "answerId") Long answerId) {
        return bookmarkService.isAnswerBookmarked(authUser.getId(), commId, answerId);
    }
}
