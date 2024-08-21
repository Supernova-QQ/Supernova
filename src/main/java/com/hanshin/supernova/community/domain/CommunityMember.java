package com.hanshin.supernova.community.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community_member")
public class CommunityMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    private Autority autority;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt = super.getCreatedAt();

    @Column(name = "community_id")
    private Long communityId;

    @Column(name = "user_id")
    private Long userId;
}
