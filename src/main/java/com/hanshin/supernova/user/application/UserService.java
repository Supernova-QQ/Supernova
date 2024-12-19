package com.hanshin.supernova.user.application;

import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.ChangePasswordResponse;
import com.hanshin.supernova.user.dto.response.ResetPasswordResponse;
import com.hanshin.supernova.user.dto.response.UserRegisterResponse;
import com.hanshin.supernova.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface UserService {

    UserRegisterResponse registerUser(UserRegisterRequest request);

    User getById(Long id);

    User getByEmail(String email);

    boolean checkNickname(String nickname);

    boolean existsByEmail(String email);

    boolean validatePassword(String password);

    ChangePasswordResponse changePassword(HttpServletRequest request, String currentPassword, String newPassword, String confirmNewPassword);

    ResetPasswordResponse resetPassword(String email, String username, String newPassword, String confirmNewPassword);

//    void deleteUser(Long userId, String password);

    void deleteUser(Long userId, String password);

    public List<User> getAllUsers();

    public User getUserFromClaims(HttpServletRequest request);

    public String getNicknameById(Long userId);

//    public boolean updateUserName(Long id, String newName);
}