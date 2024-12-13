package com.hanshin.supernova.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteUserRequest {
    @NotNull(message = "User ID는 필수입니다.")
    private Long userId;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
