package com.hanshin.supernova.hashtag.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashtagSaveResponse {

    private List<String> savedHashtagNames;

    public static HashtagSaveResponse toResponse(List<String> savedHashtagNames) {
        return new HashtagSaveResponse(savedHashtagNames);
    }
}
