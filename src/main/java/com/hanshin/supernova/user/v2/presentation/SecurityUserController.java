package com.hanshin.supernova.user.v2.presentation;

import com.hanshin.supernova.user.v2.domain.SecurityUser;
import com.hanshin.supernova.user.v2.dto.request.SecurityUserRegisterRequest;
import com.hanshin.supernova.user.v2.dto.request.UserUpdatePasswordRequest;
import com.hanshin.supernova.user.v2.dto.response.SecurityUserRegisterResponse;
import com.hanshin.supernova.user.v2.application.SecurityUserService;
import com.hanshin.supernova.user.v2.dto.response.SecurityUserUpdatePasswordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/security", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SecurityUserController {

    private final SecurityUserService securityUserService;

    @PostMapping("/auth/register")
    public ResponseEntity<SecurityUserRegisterResponse> register(@RequestBody SecurityUserRegisterRequest request) {
        SecurityUserRegisterResponse response = securityUserService.registerUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/my/password_reset")
    public ResponseEntity<SecurityUserUpdatePasswordResponse> updatePassword(@Validated @RequestBody UserUpdatePasswordRequest updatePasswordRequest){
        String email = updatePasswordRequest.getEmail();
        SecurityUserUpdatePasswordResponse response = securityUserService.updatePassword(
                email,
                updatePasswordRequest.getCurrentPassword(),
                updatePasswordRequest.getNewPassword(),
                updatePasswordRequest.getConfirmPassword()
        );
        return ResponseEntity.ok(response);
    }
}
