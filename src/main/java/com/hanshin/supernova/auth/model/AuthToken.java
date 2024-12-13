package com.hanshin.supernova.auth.model;

import lombok.Data;

import static com.hanshin.supernova.auth.AuthConstants.ACCESS_TOKEN_HEADER_KEY;

@Data
public class AuthToken {
    private final String key;
    private final String token;

    public AuthToken(String token) {
        this.key = ACCESS_TOKEN_HEADER_KEY;
        this.token = token;
    }
}