package com.hanshin.supernova.notice.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NoticeResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean isPinned;

    public static NoticeResponse toResponse(Long noticeId, String title, String content,
            LocalDateTime createdAt, boolean isPinned) {
        return new NoticeResponse(noticeId, title, content, createdAt, isPinned);
    }
}
