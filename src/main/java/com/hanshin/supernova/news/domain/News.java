package com.hanshin.supernova.news.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @Enumerated
    private Type type;

    @Column(name = "is_viewed")
    private boolean isViewed;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "has_related_content")
    private boolean hasRelatedContent;

    @Column(name = "related_content_id")
    private Long relatedContentId;

    public void update(String title, String content, Type type, boolean hasRelatedContent, Long relatedContentId) {
        this.title = title;
        this.content = content;
        this.type = type;
        this.hasRelatedContent = hasRelatedContent;
        this.relatedContentId = relatedContentId;
    }

    public void changeViewed() {
        this.isViewed = true;
    }

    // 수신인 변경: 관리자 페이지에서 가능하다.
//    public void changeReciever(Long recieverId) {
//        this.recieverId = recieverId;
//    }
}
