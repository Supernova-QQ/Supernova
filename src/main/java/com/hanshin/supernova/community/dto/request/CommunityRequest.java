package com.hanshin.supernova.community.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommunityRequest {

    @NotBlank(message = "커뮤니티 이름은 필수입니다.")
    private String name;

    @Size(max = 200, message = "소개글은 최대 200자까지 작성 가능합니다.")
    private String description;

    //    private String imageUrl;

    private boolean isVisible;

    private boolean isPublic;
}
