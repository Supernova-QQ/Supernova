package com.hanshin.supernova.user.v1.application;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserRegisterInvalidException;
import com.hanshin.supernova.user.v1.domain.User;
import com.hanshin.supernova.user.v1.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.v1.dto.response.UserRegisterResponse;
import com.hanshin.supernova.user.v1.infrasturcture.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;


    public UserRegisterResponse register(@Valid UserRegisterRequest request) {

        // 이메일 중복 검증
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserRegisterInvalidException(ErrorType.EMAIL_DUPLICATE_ERROR);
        }

        // 닉네임 중복 검증
        if(userRepository.existsByNickname(request.getNickname())) {
            throw new UserRegisterInvalidException(ErrorType.NICKNAME_DUPLICATE_ERROR);
        }


        // 비밀번호 유효성 검사 / 암호화

        // user 를 빌드해서 DB 에 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .username(request.getNickname())
                .nickname(request.getNickname())
                .authority(request.getAuthority())
                .build();

        User savedUser = userRepository.save(user);


        // 컨트롤러에 반환
        return new UserRegisterResponse(savedUser.getId(), savedUser.getNickname());
    }
}
