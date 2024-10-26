package com.hanshin.supernova.user.application;

import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.auth.UserAuthManagementInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.exception.user.UserRegisterInvalidException;
import com.hanshin.supernova.security.service.JwtService;
import com.hanshin.supernova.user.domain.Activity;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.ChangePasswordResponse;
import com.hanshin.supernova.user.dto.response.ResetPasswordResponse;
import com.hanshin.supernova.user.dto.response.UserRegisterResponse;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    @Transactional
    @Override
    public UserRegisterResponse registerUser(UserRegisterRequest request) {
        // 이메일 중복 검증
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserRegisterInvalidException(ErrorType.EMAIL_DUPLICATE_ERROR);
        }

        // 닉네임 중복 검증
        if (userRepository.existsByNickname(request.getNickname())) {
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
        User user = User.builder()
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
    @Override
    public User getById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Transactional(readOnly = true)
    @Override
    public User getByEmail(String email) {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));
    }

    @Override
    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean validatePassword(String password) {
        return password.matches("^(?=.*[a-z])(?=.*\\d)[a-z\\d]{8,}$");
    }

    @Transactional
    @Override
    public ChangePasswordResponse changePassword(HttpServletRequest request, String currentPassword, String newPassword, String confirmNewPassword) {
        // 토큰을 통해 이메일 추출
        String email = (String) request.getAttribute("userEmail");

        if (email == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        User user = getByEmail(email);
        if (user == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new AuthInvalidException(ErrorType.WRONG_PASSWORD_ERROR);
        }

        if (!newPassword.equals(confirmNewPassword)) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ChangePasswordResponse("Password changed successfully");
    }

    @Transactional
    @Override
    public ResetPasswordResponse resetPassword(String email, String username, String newPassword, String confirmNewPassword) {
        User user = userRepository.findByEmailAndUsername(email, username)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));

        if (!newPassword.equals(confirmNewPassword)) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ResetPasswordResponse("Password reset successfully");
    }

    @Transactional
    @Override
    public void deleteUser(Long userId, String password) {
        User user = getById(userId);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserAuthManagementInvalidException(ErrorType.PASSWORD_NOT);
        }
        userRepository.delete(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
