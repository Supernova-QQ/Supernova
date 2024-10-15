package com.hanshin.supernova.user.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
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
