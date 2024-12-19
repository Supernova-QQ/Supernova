package com.hanshin.supernova.question.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class QuestionRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    private String imgUrl;

    @NotNull(message = "커뮤니티 선택은 필수입니다.")
    private Long commId;

    @Size(max = 5, message = "해시태그는 최대 5개까지 등록이 가능합니다.")
    private List<String> hashtags;

    @JsonProperty("isAiAnswerRequested")
    private boolean isAiAnswerRequested;
}
