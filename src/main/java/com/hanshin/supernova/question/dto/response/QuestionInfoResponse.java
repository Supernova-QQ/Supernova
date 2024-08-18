package com.hanshin.supernova.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionInfoResponse {

    private Long id;
    private String title;
    private String content;

    public static QuestionInfoResponse toResponse(Long id, String title, String content) {
        return new QuestionInfoResponse(id, title, content);
    }
}
