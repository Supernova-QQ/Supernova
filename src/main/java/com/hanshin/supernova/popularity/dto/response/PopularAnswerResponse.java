package com.hanshin.supernova.popularity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularAnswerResponse {

    private Long questionId;
    private Long answerId;
    private String answer;

}
