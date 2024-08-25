package com.hanshin.supernova.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionSaveResponse {

    private Long questionId;

    public static QuestionSaveResponse toResponse(Long questionId) {
        return new QuestionSaveResponse(questionId);
    }
}
