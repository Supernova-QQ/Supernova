package com.hanshin.supernova.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String confirmNewPassword;
}
