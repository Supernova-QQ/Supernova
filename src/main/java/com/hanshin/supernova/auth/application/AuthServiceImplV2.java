package com.hanshin.supernova.auth.application;


import com.hanshin.supernova.auth.dto.request.AuthLoginRequest;
import com.hanshin.supernova.auth.dto.response.AuthLoginResponseV2;
import com.hanshin.supernova.auth.model.AuthToken;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.application.UserServiceImpl;
import com.hanshin.supernova.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Profile("v2")
@RequiredArgsConstructor
public class AuthServiceImplV2 implements AuthServiceV2{
    private final UserServiceImpl userService;
    private final TokenServiceV1 tokenService;
    private final BCryptPasswordEncoder passwordEncoder;


    public AuthLoginResponseV2 login(AuthLoginRequest request) {
        User user = userService.loadUserByEmail(request.getEmail());

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthInvalidException(ErrorType.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = tokenService.jwtBuilder(user.getId(), user.getNickname());

        // 응답 생성
        return new AuthLoginResponseV2(user.getNickname(), token);
    }


    public AuthUser getAuthUser(AuthToken token) {
        // 토큰 검증 및 사용자 정보 조회
        return tokenService.getAuthUser(token);
    }

    public void logout(String token) {
        tokenService.logout(token);
    }
}