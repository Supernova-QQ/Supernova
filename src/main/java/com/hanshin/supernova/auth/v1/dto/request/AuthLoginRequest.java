package com.hanshin.supernova.auth.v1.dto.request;

import lombok.Data;

@Data
public class AuthLoginRequest {

    private String email;
    private String password;
}
