package com.hanshin.supernova.user.v1.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import com.hanshin.supernova.user.v2.domain.Activity;
import com.hanshin.supernova.user.v2.domain.Authority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
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

}
