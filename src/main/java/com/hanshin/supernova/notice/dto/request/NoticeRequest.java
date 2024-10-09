package com.hanshin.supernova.notice.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeRequest {

    @NotBlank(message = "공지사항 제목은 필수입니다")
    private String title;

    @NotBlank(message = "공지사항 내용은 필수입니다.")
    private String content;

    @JsonProperty("isPinned")
    private boolean isPinned;
}

