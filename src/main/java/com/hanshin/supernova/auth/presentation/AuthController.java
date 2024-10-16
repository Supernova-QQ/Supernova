package com.hanshin.supernova.auth.presentation;

import com.hanshin.supernova.auth.application.AuthService;
import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hanshin.supernova.auth.AuthCostants.AUTH_TOKEN_HEADER_KEY;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {
        var response = authService.login(request);
        return ResponseDto.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTH_TOKEN_HEADER_KEY) String token) {
        authService.logout(token);
        return ResponseDto.ok("Successfully logged out");
    }
}