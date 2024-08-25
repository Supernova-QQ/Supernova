package com.hanshin.supernova.question.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.LinkedHashSet;
import lombok.Data;

@Data
public class QuestionRequest {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "커뮤니티 선택은 필수입니다.")
    private Long commId;

}
