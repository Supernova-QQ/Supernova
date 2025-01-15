package com.hanshin.supernova.user.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.dto.request.*;
import com.hanshin.supernova.user.dto.response.ChangeNicknameResponse;
import com.hanshin.supernova.user.dto.response.ChangePasswordResponse;
import com.hanshin.supernova.user.dto.response.ResetPasswordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원가입")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(required = true, description = "회원 가입 요청") @RequestBody UserRegisterRequest request) {
        var response = userService.registerUser(request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "사용자 비밀번호 변경")
    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResponse> changePassword(HttpServletRequest request,
            @Parameter(required = true, description = "비밀번호 변경 요청") @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse response = userService.changePassword(
                request,
                changePasswordRequest.getCurrentPassword(),
                changePasswordRequest.getNewPassword(),
                changePasswordRequest.getConfirmNewPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 비밀번호 초기화")
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(
            @Parameter(required = true, description = "비밀번호 초기화 요청") @RequestBody @Valid ResetPasswordRequest request) {
        ResetPasswordResponse response = userService.resetPassword(
                request.getEmail(),
                request.getUsername(),
                request.getNewPassword(),
                request.getConfirmNewPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 목록 조회")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "회원 정보 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(
            @Parameter(required = true, description = "회원 정보 삭제 요청")
            @Validated @RequestBody DeleteUserRequest deleteUserRequest) {
        userService.deleteUser(deleteUserRequest.getUserId(), deleteUserRequest.getPassword());
        return ResponseEntity.ok("유저가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "사용자 닉네입 조회")
    @GetMapping("/nickname")
    public ResponseEntity<Map<String, String>> getNickname(
            @RequestBody(required = false) Map<String, Object> request, AuthUser authUser) {
        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        // 닉네임 조회
        String nickname = userService.getNicknameById(authUser.getId());
        Map<String, String> response = new HashMap<>();
        response.put("nickname", nickname);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 닉네임 변경")
    @PostMapping("/change-nickname")
    public ResponseEntity<ChangeNicknameResponse> changeNickname(
            @Parameter(required = true, description = "사용자 닉네임 변경 요청") @RequestBody @Valid ChangeNicknameRequest request,
            AuthUser authUser) {
        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        ChangeNicknameResponse response = userService.changeNickname(
                authUser.getId(),
                request.getNewNickname());

        return ResponseEntity.ok(response);
    }

//    // 특정 회원의 이름 업데이트
//    @PutMapping("/{id}/update-name")
//    public String updateUserName(@PathVariable Long id, @RequestBody UpdateNameRequest request) {
//        String newName = request.getNewName(); // JSON에서 파싱된 값
//        boolean isUpdated = userService.updateUserName(id, newName);
//        if (isUpdated) {
//            return "회원 이름이 성공적으로 업데이트되었습니다.";
//        } else {
//            return "업데이트 실패: 해당 회원을 찾을 수 없습니다.";
//        }
//    }
}
