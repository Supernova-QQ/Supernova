package com.hanshin.supernova.auth.service;

import com.hanshin.supernova.auth.model.AuthUserImpl;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // userRepository를 사용해서 이메일로 사용자를 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR));

        // 조회된 사용자를 AuthUserImpl로 변환해서 반환
        return new AuthUserImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getAuthority()
        );
    }
}
