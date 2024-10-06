package com.hanshin.supernova.auth.v2.model;

import lombok.Data;

@Data
public class SecurityAuthUserImpl implements SecurityAuthUser {
    private final Long id;

    @Override
    public Long getId() {
        return this.id;
    }
}