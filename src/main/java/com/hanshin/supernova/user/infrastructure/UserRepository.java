package com.hanshin.supernova.user.infrastructure;

import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    // 이메일과 username으로 사용자 찾기 (메서드 수정)
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.username = :username")
    Optional<User> findByEmailAndUsername(String email, String username);

    @Transactional
    @Modifying
    @Query("update User u set u.password = ?2 where u.email = ?1")
    int updatePasswordByEmail(@NonNull String email, @NonNull String password);

    Optional<User> findByAuthority(Authority authority);
}
