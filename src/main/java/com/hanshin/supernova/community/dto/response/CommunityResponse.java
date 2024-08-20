package com.hanshin.supernova.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunityResponse {

    private Long id;

    public static CommunityResponse toResponse(Long id) {
        return new CommunityResponse(id);
    }
}
