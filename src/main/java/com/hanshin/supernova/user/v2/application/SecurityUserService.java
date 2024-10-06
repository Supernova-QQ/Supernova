package com.hanshin.supernova.user.v2.application;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserRegisterInvalidException;
import com.hanshin.supernova.user.v2.domain.SecurityUser;
import com.hanshin.supernova.user.v2.domain.Authority;
import com.hanshin.supernova.user.v2.dto.request.SecurityUserRegisterRequest;
import com.hanshin.supernova.user.v2.dto.response.SecurityUserRegisterResponse;
import com.hanshin.supernova.user.v2.infrastructure.SecurityUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements SecurityUserServiceInterface {

    private final SecurityUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SecurityUserRegisterResponse registerUser(@Valid SecurityUserRegisterRequest request) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserRegisterInvalidException(ErrorType.EMAIL_DUPLICATE_ERROR);
        }

        // 닉네임 중복 검증
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new UserRegisterInvalidException(ErrorType.NICKNAME_DUPLICATE_ERROR);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 권한이 없으면 기본적으로 USER 권한 부여
        Authority authority = request.getAuthority() != null ? request.getAuthority() : Authority.USER;

        // 사용자 정보를 SecurityUser 객체로 변환
        SecurityUser user = SecurityUser.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .nickname(request.getNickname())
                .authority(authority)
                .build();
        // 사용자 저장
        SecurityUser savedUser = userRepository.save(user);

        // 응답 생성
        return new SecurityUserRegisterResponse(savedUser.getId(), savedUser.getNickname());
    }

    @Override
    public SecurityUser loadUserByEmail(String email) throws UsernameNotFoundException {
        // 이메일로 사용자 조회
        SecurityUser user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(ErrorType.USER_NOT_FOUND_ERROR.getMessage());
        }
        return user;
    }
}