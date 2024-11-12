package com.hanshin.supernova.ai_comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiAnswerResponse {

    private String answer;

    public static AiAnswerResponse toResponse(
            String answer
    ) {
        return new AiAnswerResponse(answer);
    }
}
