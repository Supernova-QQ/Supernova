package com.hanshin.supernova.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionSaveResponse {

    private Long questionId;
    private String title;
    private String content;
    private Long commId;

    public static QuestionSaveResponse toResponse(Long questionId, String title, String content, Long commId) {
        return new QuestionSaveResponse(questionId, title, content, commId);
    }
}
