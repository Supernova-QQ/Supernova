package com.hanshin.supernova.user.infrastructure;

import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.email = :email AND u.nickname = :nickname")
    Optional<String> findEmailByNameAndNickname(String email, String nickname);

    Optional<User> findByAuthority(Authority authority);
}
