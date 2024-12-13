package com.hanshin.supernova.auth.model;

import com.hanshin.supernova.user.domain.Authority;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@RequiredArgsConstructor
public class AuthUserImpl implements AuthUser, UserDetails {

    @NotNull
    private final Long id;
    private final String email;
    private final String password;
    private final Authority authority;

    // AuthUser 인터페이스 메서드
    @Override
    public Long getId() {
        return this.id;
    }

    // UserDetails 인터페이스 메서드 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한을 설정, 여기서는 간단하게 하나의 권한을 반환하도록 함
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + authority.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 계정 만료 여부 (true로 설정해 만료되지 않음)
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부 (true로 설정해 잠기지 않음)
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 자격 증명(비밀번호) 만료 여부 (true로 설정해 만료되지 않음)
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부 (true로 설정해 활성화 상태 유지)
        return true;
    }
}