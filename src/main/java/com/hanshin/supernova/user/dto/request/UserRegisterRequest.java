package com.hanshin.supernova.user.dto.request;

import com.hanshin.supernova.user.domain.Authority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data   // getter +  setter
public class UserRegisterRequest {

    @NotNull(message = "이메일은 필수입니다.")
    private String email;

    @NotNull(message = "비밀번호는 필수입니다.")
    private String password;

    @NotNull(message = "사용자 이름은 필수입니다.")
    @Size(max = 5, message = "사용자의 이름은 최대 5글자 입니다.")
    private String username;

    @NotNull(message = "닉네임은 필수입니다.")
    @Size(max = 10, message = "닉네임은 최대 10글자 입니다.")
    private String nickname;

    @NotNull(message = "사용자의 권한 설정은 필수입니다. 일반 사용자의 경우 'USER' 권한으로 추가해주세요.")
    private Authority authority;
}
