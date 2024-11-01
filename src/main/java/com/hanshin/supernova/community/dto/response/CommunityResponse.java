package com.hanshin.supernova.community.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hanshin.supernova.community.domain.CommCounter;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunityResponse {

    private Long id;

    private String name;

    private String description;

    private String imgUrl;

    private LocalDateTime createdAt;

    @JsonProperty("isVisible")
    private boolean isVisible;

    @JsonProperty("isPublic")
    private boolean isPublic;

    @JsonProperty("isDormant")
    private boolean isDormant;

    private CommCounter commCounter;

    public static CommunityResponse toResponse(
            Long id,
            String name,
            String description,
            String imgUrl,
            LocalDateTime createdAt,
            boolean isVisible,
            boolean isPublic,
            boolean isDormant,
            CommCounter commCounter
    ) {
        return new CommunityResponse(id, name, description, imgUrl, createdAt, isVisible, isPublic,
                isDormant, commCounter);
    }
}
