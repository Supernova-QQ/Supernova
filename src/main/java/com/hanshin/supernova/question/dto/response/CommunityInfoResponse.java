package com.hanshin.supernova.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunityInfoResponse {

    private Long cId;
    private String cName;

    public static CommunityInfoResponse toResponse(
            Long communityId, String cName
    ) {
        return new CommunityInfoResponse(communityId, cName);
    }
}
