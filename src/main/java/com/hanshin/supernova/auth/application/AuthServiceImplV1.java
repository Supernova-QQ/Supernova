package com.hanshin.supernova.auth.application;


import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponseV2;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("v1")
@RequiredArgsConstructor
public class AuthServiceImplV1{

    private final UserRepository userRepository;
    private final TokenServiceV1 tokenService;

    public AuthLoginResponseV2 login(AuthLoginRequest request){
        var user = getUserByEmail(request.getEmail());

        verifyPassword(request.getPassword(), user.getPassword());

        var token = getTokenByUser(user);

        return new AuthLoginResponseV2(user.getNickname(), token);
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