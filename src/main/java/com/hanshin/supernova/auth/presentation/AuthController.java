package com.hanshin.supernova.auth.presentation;

import com.hanshin.supernova.auth.application.AuthServiceImplV3;
import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponse;
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
    private final AuthServiceImplV3 authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request){
        AuthLoginResponse response = authService.login(request);
        return ResponseDto.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTH_TOKEN_HEADER_KEY) String token) {
        authService.logout(token);
        return ResponseDto.ok("Successfully logged out");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Refresh-Token") String refreshToken) {
        AuthLoginResponse response = authService.refreshToken(refreshToken);
        return ResponseDto.ok(response);
    }
}
