package com.hanshin.supernova.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginResponse {
    private String nickname;      // 사용자 닉네임
    private String accessToken;   // 액세스 토큰
    private String refreshToken;  // 리프레시 토큰
}
