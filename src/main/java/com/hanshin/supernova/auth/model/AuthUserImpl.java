package com.hanshin.supernova.auth.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthUserImpl implements AuthUser {

    @NotNull
    private final Long id;
}