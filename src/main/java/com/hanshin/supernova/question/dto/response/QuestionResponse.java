package com.hanshin.supernova.question.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionResponse {

    private String title;
    private String content;
    private String imgUrl;
    private boolean isResolved;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int viewCnt;
    private int recCnt;
    private Long commId;
    private Long questionerId;
    private String commName;
    private String questionerName;

    public static QuestionResponse toResponse(
            String title,
            String content,
            String imgUrl,
            boolean isResolved,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            int viewCnt,
            int recCnt,
            Long commId,
            Long questionerId,
            String commName,
            String questionerName
    ) {
        return new QuestionResponse(title, content, imgUrl, isResolved, createdAt, modifiedAt, viewCnt,
                recCnt, commId, questionerId, commName, questionerName);
    }
}
