package com.hanshin.supernova.answer.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiCommentRequest {

    private Long questionId;
    private String aiAnswer;
}
