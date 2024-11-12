package com.hanshin.supernova.news.dto.response;

import com.hanshin.supernova.news.domain.Type;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewsResponse {

    private Long id;

    private String title;

    private String content;

    private Type type;

    private boolean isViewed;

    private LocalDateTime createdAt;

    private boolean hasRelatedContent;

    private Long relatedContentId;

    public static NewsResponse toResponse(Long id, String title, String content, Type type,
            boolean isViewed, LocalDateTime createdAt, boolean hasRelatedContent,
            Long relatedContentId) {
        return new NewsResponse(id, title, content, type, isViewed, createdAt, hasRelatedContent,
                relatedContentId);
    }
}
