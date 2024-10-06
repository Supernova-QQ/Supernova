package com.hanshin.supernova.user.v1.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.user.v1.application.UserService;
import com.hanshin.supernova.user.v1.dto.request.UserRegisterRequest;
import com.hanshin.supernova.user.v1.dto.response.UserRegisterResponse;
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

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody @Valid UserRegisterRequest request){
        UserRegisterResponse response = userService.register(request);
        return ResponseDto.created(response);
    }
}
