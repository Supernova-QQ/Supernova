package com.hanshin.supernova.my.dto.response;

import com.hanshin.supernova.community.domain.Community;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyCommunityResponse {
    private Long id;
    private String name;
    private String description;
    private String imgUrl;

    public static MyCommunityResponse toResponse(Community community) {
        return new MyCommunityResponse(
                community.getId(),
                community.getName(),
                community.getDescription(),
                community.getImgUrl()
        );
    }
}
