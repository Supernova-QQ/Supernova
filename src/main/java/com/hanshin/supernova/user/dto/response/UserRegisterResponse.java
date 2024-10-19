package com.hanshin.supernova.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRegisterResponse {

    @JsonProperty("userId")
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String password;

    public static UserRegisterResponse toResponse(Long userId, String username, String nickname, String email, String password
    ) {
        return new UserRegisterResponse(userId, username, nickname, email, password);
    }
}
