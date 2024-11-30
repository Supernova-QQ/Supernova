package com.hanshin.supernova.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class AuthorizeToken extends AccessTokenWrapper {

    @JsonProperty("refresh_token")
    private final String refreshToken;

    public AuthorizeToken(String accessToken, String refreshToken) {
        super(accessToken);
        this.refreshToken = refreshToken;
    }
}