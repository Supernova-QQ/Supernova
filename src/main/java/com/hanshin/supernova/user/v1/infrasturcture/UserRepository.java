package com.hanshin.supernova.user.v1.infrasturcture;

import com.hanshin.supernova.user.v2.domain.Authority;
import com.hanshin.supernova.user.v1.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional<User> findByAuthority(Authority authority);
}
