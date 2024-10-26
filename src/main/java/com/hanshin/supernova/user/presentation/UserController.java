package com.hanshin.supernova.user.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.dto.request.ChangePasswordRequest;
import com.hanshin.supernova.user.dto.request.ResetPasswordRequest;
import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.ChangePasswordResponse;
import com.hanshin.supernova.user.dto.response.ResetPasswordResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        var response = userService.registerUser(request);
        return ResponseDto.created(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(HttpServletRequest request, @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse response = userService.changePassword(
                request,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        ResetPasswordResponse response = userService.resetPassword(
                request.getEmail(),
                request.getUsername(),
                request.getNewPassword(),
                request.getConfirmNewPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
