package com.hanshin.supernova.bookmark.dto.request;

import com.hanshin.supernova.bookmark.domain.BookmarkType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    private Long targetId; // 질문/답변 ID
    private BookmarkType type; // 질문/답변 구분
    private Long commId; // 커뮤니티 ID
}

