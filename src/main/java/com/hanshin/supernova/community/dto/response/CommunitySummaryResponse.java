package com.hanshin.supernova.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunitySummaryResponse {

    private Long id;
    private String name;
    private int userCnt;
//    private String profileImg;

    public static CommunitySummaryResponse toResponse(
            Long id, String name, int userCnt
    ) {
        return new CommunitySummaryResponse(id, name, userCnt);
    }
}
