package com.hanshin.supernova.bookmark.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // 로그인한 유저 ID

    @Column(nullable = false)
    private Long targetId; // 질문 또는 답변의 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookmarkType type; // 질문인지 답변인지 구분

    @Column(nullable = false)
    private Long commId; // 커뮤니티 ID
}
