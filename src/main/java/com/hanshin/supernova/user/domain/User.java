package com.hanshin.supernova.user.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    private String password;

    private String username;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Enumerated
    private Authority authority;

    @Embedded
    private Activity activity;

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }
}
