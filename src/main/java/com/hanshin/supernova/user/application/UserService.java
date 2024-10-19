package com.hanshin.supernova.user.application;

import com.hanshin.supernova.exception.auth.UserAuthManagementInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.exception.user.UserRegisterInvalidException;
import com.hanshin.supernova.user.domain.Activity;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.UserRegisterResponse;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResponse registerUser(UserRegisterRequest request) {

        // 이메일 중복 검증
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new UserRegisterInvalidException(ErrorType.EMAIL_DUPLICATE_ERROR);
        }

        // 닉네임 중복 검증
        if(userRepository.existsByNickname(request.getNickname())) {
            throw new UserRegisterInvalidException(ErrorType.NICKNAME_DUPLICATE_ERROR);
        }

        validatePassword(request.getPassword());

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserRegisterInvalidException(ErrorType.PASSWORD_NOT);
        }

        User savedUser = buildAndSaveUser(request);

        return UserRegisterResponse.toResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getNickname(),
                savedUser.getEmail(),
                savedUser.getPassword());
    }

    private User buildAndSaveUser(UserRegisterRequest request) {
        Activity newActivity = new Activity();
        User user =
                User.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .username(request.getUsername())
                        .nickname(request.getNickname())
                        .authority(request.getAuthority())
                        .activity(newActivity)
                        .build();
        return userRepository.save(user);
    }
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public String getEmailByNameAndNickname(String name, String nickname) {
        return userRepository
                .findEmailByNameAndNickname(name, nickname)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$");
    }

    @Transactional
    public void updatePassword(String email, String newPassword) {
        int result = userRepository.updatePasswordByEmail(email, newPassword);
        if (result <= 0) {
            throw new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = getById(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void deleteUser(Long userId, String password) {
        User user = getById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }
        userRepository.delete(user);
    }

}