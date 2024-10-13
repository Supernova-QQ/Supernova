package com.hanshin.supernova.auth.v1.presentation;

import com.hanshin.supernova.auth.v2.application.SecurityAuthService;
import com.hanshin.supernova.auth.v2.dto.request.SecurityAuthLoginRequest;
import com.hanshin.supernova.common.model.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hanshin.supernova.auth.AuthCostants.AUTH_TOKEN_HEADER_KEY;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final SecurityAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SecurityAuthLoginRequest request) {
        var response = authService.login(request);
        return ResponseDto.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTH_TOKEN_HEADER_KEY) String token) {
        authService.logout(token);
        return ResponseDto.ok("Successfully logged out");
    }
}