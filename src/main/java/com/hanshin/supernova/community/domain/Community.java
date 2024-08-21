package com.hanshin.supernova.community.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "community")
public class Community extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    private String description;

//    private MultipartFile profileImg;  // 커뮤니티 프로필 이미지

    @Column(name = "is_visible")
    private boolean isVisible; // 외부인의 조회 가능 여부

    @Column(name = "is_public")
    private boolean isPublic;   // 외부인의 게시글 등록 가능 여부

    @Column(name = "is_dormant")
    private boolean isDormant;  // 휴면 여부

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Embedded
    private CommCounter commCounter;

    public void update(
            String name, String description, boolean isVisible, boolean isPublic) {
        this.name = name;
        this.description = description;
        this.isVisible = isVisible;
        this.isPublic = isPublic;
    }

    public void changeDormant() {
        this.isDormant = true;
    }
}
