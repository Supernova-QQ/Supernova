package com.hanshin.supernova.community.dto.response;

import com.hanshin.supernova.community.domain.CommCounter;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommunityInfoResponse {

    private String name;
    private String description;
    private LocalDateTime createdAt;
    private boolean isVisible;
    private boolean isPublic;
    private boolean isDormant;
    private CommCounter commCounter;

    public static CommunityInfoResponse toResponse(
            String name,
            String description,
            LocalDateTime createdAt,
            boolean isVisible,
            boolean isPublic,
            boolean isDormant,
            CommCounter commCounter
    ) {
        return new CommunityInfoResponse(name, description, createdAt, isVisible, isPublic,
                isDormant, commCounter);
    }
}
