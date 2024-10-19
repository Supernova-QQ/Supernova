package com.hanshin.supernova.auth.presentation;

import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.security.TokenConstants;
import com.hanshin.supernova.security.model.AccessTokenWrapper;
import com.hanshin.supernova.security.model.AuthorizeToken;
import com.hanshin.supernova.security.service.JwtService;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.domain.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

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

    @SneakyThrows
    @PostMapping("/refresh")
    public void refresh(@RequestBody AccessTokenWrapper accessToken, @CookieValue(name = TokenConstants.REFRESH_TOKEN_COOKIE_NAME) String refreshToken, HttpServletResponse response) {
        AuthorizeToken oldAuthorizeToken = new AuthorizeToken(accessToken.getAccessToken(), refreshToken);
        String newAccessToken = jwtService.refresh(oldAuthorizeToken);
        String newRefreshToken = jwtService.getRefreshToken();
        AuthorizeToken authorizeToken = new AuthorizeToken(newAccessToken, newRefreshToken);
        jwtService.setTokenPair(authorizeToken);
        jwtService.writeResponse(response, authorizeToken);
    }
}