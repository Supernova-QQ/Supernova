package com.hanshin.supernova.auth.v1.application;

import com.hanshin.supernova.auth.v1.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.v1.dto.response.AuthLoginResponse;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.v1.domain.User;
import com.hanshin.supernova.user.v1.infrasturcture.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public AuthLoginResponse login(AuthLoginRequest request) {

        var user = getUserByEmail(request.getEmail());

        verifyPassword(request.getPassword(), user.getPassword());

        var token = getTokenByUser(user);

        return new AuthLoginResponse(user.getNickname(), token);
    }

    private User getUserByEmail(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
        return user;
    }

    private void verifyPassword(String reqPassword, String userPassword) {
        if (!reqPassword.equals(userPassword)) {
            throw new AuthInvalidException(ErrorType.INVALID_PASSWORD);
        }
    }

    private String getTokenByUser(User user) {
        return tokenService.jwtBuilder(user.getId(), user.getNickname());
    }

    public void logout(String token) {
        tokenService.logout(token);
    }
}