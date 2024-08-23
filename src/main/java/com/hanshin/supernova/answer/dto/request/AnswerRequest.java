package com.hanshin.supernova.answer.dto.request;

import com.hanshin.supernova.answer.domain.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnswerRequest {

    @Size(max = 5000)
    @NotBlank
    private String answer;

    private Tag tag;

    @Size(max = 255)    // 대부분의 URL 은 255자 이내
    private String source;
}
