package com.hanshin.supernova.notice.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.notice.application.NoticeService;
import com.hanshin.supernova.notice.dto.request.NoticeRequest;
import com.hanshin.supernova.notice.dto.response.NoticeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 생성")
    @PostMapping
    public ResponseEntity<?> createNotice(
            @Parameter(required = true, description = "공지사항 생성 요청")
            AuthUser user, @RequestBody @Valid NoticeRequest request
    ) {
        var response = noticeService.createNotice(user, request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "공지사항 조회")
    @GetMapping("/{noticeId}")
    public ResponseEntity<?> getNotice(
            @Parameter(description = "공지사항 고유 번호")
            @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.getNotice(noticeId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "공지사항 수정")
    @PutMapping("/{noticeId}")
    public ResponseEntity<?> updateNotice(
            @Parameter(description = "공지사항 고유 번호")
            AuthUser user, @PathVariable("noticeId") Long noticeId,
            @Parameter(required = true, description = "공지사항 수정 요청")
            @RequestBody @Valid NoticeRequest request
    ) {
        var response = noticeService.updateNotice(user, noticeId, request);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "공지사항 삭제")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> deleteNotice(
            @Parameter(description = "공지사항 고유 번호")
            AuthUser user, @PathVariable("noticeId") Long noticeId
    ) {
        var response = noticeService.deleteNotice(user, noticeId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "공지사항 목록 최신순으로 조회")
    @GetMapping
    public ResponseEntity<?> getAllNotices() {
        List<NoticeResponse> responses = noticeService.getAllNotices();
        return ResponseDto.ok(responses);
    }

    @Operation(summary = "고정된 공지사항 목록 조회")
    @GetMapping("/pinned-list")
    public ResponseEntity<?> getPinnedNotices() {
        List<NoticeResponse> responses = noticeService.getPinnedNotices();
        return ResponseDto.ok(responses);
    }
}
