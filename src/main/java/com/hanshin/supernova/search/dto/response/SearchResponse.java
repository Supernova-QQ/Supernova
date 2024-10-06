package com.hanshin.supernova.search.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchResponse {

    private Long questionId;
    private String title;
    private String content;
    private int answerCnt;
    private int viewCnt;
    private int recommendationCnt;
    private String communityName;
    private LocalDateTime createdAt;

    public static SearchResponse toResponse(
            Long questionId, String title, String contents, int answerCnt, int viewCnt, int recommendationCnt, String communityName, LocalDateTime createdAt
    ) {
        return new SearchResponse(questionId, title, contents, answerCnt, viewCnt, recommendationCnt, communityName, createdAt);
    }
}
