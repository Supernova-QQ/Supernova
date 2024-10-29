package com.hanshin.supernova.question.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionInfoResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String questionerName;

    public static QuestionInfoResponse toResponse(Long id, String title, String content, LocalDateTime createdAt, String questionerName) {
        return new QuestionInfoResponse(id, title, content, createdAt, questionerName);
    }
}
