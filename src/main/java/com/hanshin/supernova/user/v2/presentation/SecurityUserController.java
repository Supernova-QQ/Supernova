package com.hanshin.supernova.user.v2.presentation;

import com.hanshin.supernova.user.v2.dto.request.SecurityUserRegisterRequest;
import com.hanshin.supernova.user.v2.dto.response.SecurityUserRegisterResponse;
import com.hanshin.supernova.user.v2.application.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/security/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SecurityUserController {

    private final SecurityUserService securityUserService;

    @PostMapping
    public ResponseEntity<SecurityUserRegisterResponse> register(@RequestBody SecurityUserRegisterRequest request) {
        SecurityUserRegisterResponse response = securityUserService.registerUser(request);
        return ResponseEntity.ok(response);
    }
}
