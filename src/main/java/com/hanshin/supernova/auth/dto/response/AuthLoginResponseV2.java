package com.hanshin.supernova.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthLoginResponseV2 {
    private String nickname;      // 사용자 닉네임
    private String token;   // 토큰
}
