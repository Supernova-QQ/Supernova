package com.hanshin.supernova.auth.v2.application;

import com.hanshin.supernova.auth.v2.dto.request.SecurityAuthLoginRequest;
import com.hanshin.supernova.auth.v2.dto.response.SecurityAuthLoginResponse;
import com.hanshin.supernova.auth.v2.model.SecurityAuthToken;
import com.hanshin.supernova.auth.v2.model.SecurityAuthUser;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.v2.application.SecurityUserServiceInterface;
import com.hanshin.supernova.user.v2.domain.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class SecurityAuthService {
    private final SecurityUserServiceInterface userService;
    private final SecurityTokenService tokenService;
    private final BCryptPasswordEncoder passwordEncoder;


    public SecurityAuthLoginResponse login(SecurityAuthLoginRequest request) {
        SecurityUser user = userService.loadUserByEmail(request.getEmail());

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthInvalidException(ErrorType.INVALID_PASSWORD);
        }

        // JWT 토큰 생성
        String token = tokenService.jwtBuilder(user.getId(), user.getNickname());

        // 응답 생성
        return new SecurityAuthLoginResponse(user.getNickname(), token);
    }


    public SecurityAuthUser getAuthUser(SecurityAuthToken token) {
        // 토큰 검증 및 사용자 정보 조회
        return tokenService.getAuthUser(token);
    }

    public void logout(String token) {
        tokenService.logout(token);
    }
}