package com.hanshin.supernova.user.dto.response;

import com.hanshin.supernova.user.domain.User;
import lombok.Data;

@Data
public class UserProfileResponse {
    private final String profileImageUrl;

    public UserProfileResponse(User user) {
        this.profileImageUrl = user.getProfileImageUrl();
    }
}
