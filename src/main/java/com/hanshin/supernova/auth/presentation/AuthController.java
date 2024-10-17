package com.hanshin.supernova.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanshin.supernova.auth.application.AuthService;
import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.security.model.AuthorizeToken;
import com.hanshin.supernova.security.service.JwtService;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.hanshin.supernova.auth.AuthCostants.AUTH_TOKEN_HEADER_KEY;

@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @SneakyThrows
    @PostMapping("/login")
    public void login(@RequestBody AuthLoginRequest loginRequest, HttpServletResponse response) {
        User user = userService.getByEmail(loginRequest.getEmail());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthInvalidException(ErrorType.WRONG_PASSWORD_ERROR);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.getRefreshToken();
        AuthorizeToken authorizeToken = new AuthorizeToken(accessToken, refreshToken);
        jwtService.setTokenPair(authorizeToken);
        jwtService.writeResponse(response, authorizeToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(AUTH_TOKEN_HEADER_KEY) String token) {
        authService.logout(token);
        return ResponseDto.ok("Successfully logged out");
    }
}