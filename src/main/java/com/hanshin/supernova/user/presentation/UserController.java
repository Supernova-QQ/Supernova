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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
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

    //    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteUser(@Validated @RequestBody DeleteUserRequest deleteUserRequest) {
//        userService.deleteUser(deleteUserRequest.getUserId(), deleteUserRequest.getPassword());
//        return ResponseEntity.ok("유저가 성공적으로 삭제되었습니다.");
//    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@Validated @RequestBody DeleteUserRequest deleteUserRequest) {
        userService.deleteUser(deleteUserRequest.getUserId(), deleteUserRequest.getPassword());
        return ResponseEntity.ok("유저가 성공적으로 삭제되었습니다.");
    }

    @GetMapping("/nickname")
    public ResponseEntity<Map<String, String>> getNickname(@RequestBody(required = false) Map<String, Object> request, AuthUser authUser) {
        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        // 닉네임 조회
        String nickname = userService.getNicknameById(authUser.getId());
        Map<String, String> response = new HashMap<>();
        response.put("nickname", nickname);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-nickname")
    public ResponseEntity<ChangeNicknameResponse> getNickname(@RequestBody @Valid ChangeNicknameRequest request, AuthUser authUser) {
        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        log.info("controller new Nickname:{}",request.getNewNickname());
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
