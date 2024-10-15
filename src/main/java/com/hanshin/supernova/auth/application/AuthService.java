package com.hanshin.supernova.auth.application;

import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponse;


public interface AuthService {
    AuthLoginResponse login(AuthLoginRequest request);
    void logout(String token);
}
