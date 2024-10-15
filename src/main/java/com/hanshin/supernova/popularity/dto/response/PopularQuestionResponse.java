package com.hanshin.supernova.popularity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularQuestionResponse {

    private Long id;
    private String title;
    private String content;
    private Long viewCnt;
}
