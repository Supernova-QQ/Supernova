package com.hanshin.supernova.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangeNicknameRequest {
    @NotBlank(message = "새로운 닉네임을 입력하세요.")
    private String newNickname;
}
