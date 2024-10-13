package com.hanshin.supernova.auth.model;

import com.hanshin.supernova.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private Long expiryDate; // 만료 시간

    @ManyToOne
    private User user;

}
