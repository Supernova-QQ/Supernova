package com.hanshin.supernova.answer.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AiCommentResponse {

    private Long id;
    private String nickname;
    private String answer;
    private LocalDateTime createdAt;

    public static AiCommentResponse toResponse(Long id, String nickname, String answer,
            LocalDateTime createdAt) {
        return new AiCommentResponse(id, nickname, answer, createdAt);
    }

}
