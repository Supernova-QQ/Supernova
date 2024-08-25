package com.hanshin.supernova.question.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionResponse {

    private String title;
    private String content;
    private boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCnt;
    private int recCnt;
    private Long commId;
//    private String questionerId;

    public static QuestionResponse toResponse(
            String title,
            String content,
            boolean isResolved,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            int viewCnt,
            int recCnt,
            Long commId
    ) {
        return new QuestionResponse(title, content, isResolved, createdAt, modifiedAt, viewCnt,
                recCnt, commId);
    }
}
