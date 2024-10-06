package com.hanshin.supernova.auth.v2.dto.request;

import lombok.Data;

@Data
public class SecurityAuthLoginRequest {

    private String email;
    private String password;
}
