package com.hanshin.supernova.auth.v2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityAuthLoginResponse {

    private String nickname;
    private String token;
}
