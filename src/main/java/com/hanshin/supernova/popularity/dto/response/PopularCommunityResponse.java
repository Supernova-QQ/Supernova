package com.hanshin.supernova.popularity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopularCommunityResponse {

    private Long id;
    private String name;
    private int memberCnt;
    private Long visitorCnt;
}
