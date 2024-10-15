package com.hanshin.supernova.auth.application;

import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponseV2;


public interface AuthServiceV2 {
    AuthLoginResponseV2 login(AuthLoginRequest request);
    void logout(String token);
}
