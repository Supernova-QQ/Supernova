package com.hanshin.supernova.user.v2.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdatePasswordRequest {
    @NotNull
    private String email;
    @NotNull
    private String currentPassword;
    @NotNull
    private String newPassword;
    @NotNull
    private String confirmPassword;
}
