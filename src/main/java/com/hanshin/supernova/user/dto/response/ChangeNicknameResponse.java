package com.hanshin.supernova.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeNicknameResponse {
    private String message;
    private String newNickname;
}