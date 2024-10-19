package com.hanshin.supernova.user.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.user.application.UserService;
import com.hanshin.supernova.user.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.dto.response.UserRegisterResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
