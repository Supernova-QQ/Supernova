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
//@RequestMapping("/communities/{communityId}/my-note/bookmarks")
//public class BookmarkViewController {
//
//    private final BookmarkService bookmarkService;
//
//    /**
//     * 특정 커뮤니티에서 북마크된 질문과 답변 조회
//     */
//    @GetMapping
//    public ResponseEntity<List<Bookmark>> getCommunityBookmarks(
//            @PathVariable Long communityId,
//            @RequestAttribute("httpRequest") HttpServletRequest httpRequest) {
//        List<Bookmark> bookmarks = bookmarkService.getCommunityBookmarks(communityId, httpRequest);
//        return ResponseEntity.ok(bookmarks);
//    }
//}
