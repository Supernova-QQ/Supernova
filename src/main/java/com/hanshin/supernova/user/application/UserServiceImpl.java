package com.hanshin.supernova.user.application;


import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.news.NewsInvalidException;
import com.hanshin.supernova.exception.user.UserRegisterInvalidException;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.UserRegisterResponse;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegisterResponse registerUser(@Valid UserRegisterRequest request) {
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
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .username(request.getUsername())
                .nickname(request.getNickname())
                .authority(authority)
                .build();
        // 사용자 저장
        User savedUser = userRepository.save(user);

        // 응답 생성
        return new UserRegisterResponse(savedUser.getId(), savedUser.getNickname());
    }

    public User loadUserByEmail(String email){
        // 이메일로 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorType.USER_NOT_FOUND_ERROR.getMessage()));

        return user;
    }
}
