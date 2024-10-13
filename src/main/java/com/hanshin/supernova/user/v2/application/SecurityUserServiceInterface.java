package com.hanshin.supernova.user.v2.application;

import com.hanshin.supernova.user.v2.domain.SecurityUser;
import com.hanshin.supernova.user.v2.dto.request.SecurityUserRegisterRequest;
import com.hanshin.supernova.user.v2.dto.response.SecurityUserRegisterResponse;

public interface SecurityUserServiceInterface {
    SecurityUserRegisterResponse registerUser(SecurityUserRegisterRequest request);

    SecurityUser loadUserByEmail(String email);
}

