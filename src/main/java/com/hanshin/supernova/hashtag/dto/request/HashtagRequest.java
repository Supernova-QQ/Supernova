package com.hanshin.supernova.hashtag.dto.request;

import static com.hanshin.supernova.hashtag.HashtagConstants.QUESTION_HASHTAG_MAX_SIZE;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class HashtagRequest {

    @Size(max = QUESTION_HASHTAG_MAX_SIZE, message = "해시태그는 최대 5개까지 등록 가능합니다.")
    private List<String> hashtagNames;
}
