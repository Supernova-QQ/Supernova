package com.hanshin.supernova.auth.v2.model;

import lombok.Data;

import static com.hanshin.supernova.auth.AuthCostants.AUTH_TOKEN_HEADER_KEY;

@Data
public class SecurityAuthToken {
    private final String key;
    private final String token;

    public SecurityAuthToken(String token) {
        this.key = AUTH_TOKEN_HEADER_KEY;
        this.token = token;
    }
}