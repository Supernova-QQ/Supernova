package com.hanshin.supernova.user.v2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SecurityUserRegisterResponse {

    private Long id;
    private String nickname;
}