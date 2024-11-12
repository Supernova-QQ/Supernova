package com.hanshin.supernova.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunityInfoResponse {

    private Long id;
    private String name;
    private int userCnt;
    private String imgUrl;

    public static CommunityInfoResponse toResponse(
            Long id, String name, int userCnt, String imgUrl
    ) {
        return new CommunityInfoResponse(id, name, userCnt, imgUrl);
    }
}
