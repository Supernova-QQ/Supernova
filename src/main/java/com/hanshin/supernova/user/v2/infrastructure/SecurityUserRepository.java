package com.hanshin.supernova.user.v2.infrastructure;

import com.hanshin.supernova.user.v2.domain.Authority;
import com.hanshin.supernova.user.v2.domain.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);

    SecurityUser findByEmail(String email);
    SecurityUser findByAuthority(Authority authority);
}
