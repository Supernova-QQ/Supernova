package com.hanshin.supernova.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginResponse {

    private String nickname;
    private String accessToken;
    private String refreshToken;
}
