package com.hanshin.supernova.notice.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.notice.application.NoticeService;
import com.hanshin.supernova.notice.dto.request.NoticeRequest;
import com.hanshin.supernova.notice.dto.response.NoticeResponse;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping(path = "/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공지사항 생성
    @PostMapping
    public ResponseEntity<?> createNotice(
            AuthUser user, @RequestBody @Valid NoticeRequest request
    ) {
        var response = noticeService.createNotice(user.getId(), request);
        return ResponseDto.created(response);
    }

    // 공지사항 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> getNotice(
            @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.getNotice(noticeId);
        return ResponseDto.ok(response);
    }

    // 공지사항 수정
    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(
            AuthUser user, @PathVariable("noticeId") Long noticeId,
            @RequestBody @Valid NoticeRequest request
    ) {
        var response = noticeService.updateNotice(user.getId(), noticeId, request);
        return ResponseDto.ok(response);
    }

    // 공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(
            AuthUser user, @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.deleteNotice(user.getId(), noticeId);
        return ResponseDto.ok(response);
    }

    // 공지사항 목록 최신순
    @GetMapping
    public ResponseEntity<?> getAllNotices() {
        List<NoticeResponse> responses = noticeService.getAllNotices();
        return ResponseDto.ok(responses);
    }

    // 고정된 공지사항 목록
    @GetMapping("/pinned-list")
    public ResponseEntity<?> getPinnedNotices() {
        List<NoticeResponse> responses = noticeService.getPinnedNotices();
        return ResponseDto.ok(responses);
    }
}
