package com.hanshin.supernova.question.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Question extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    // 콘텐츠 정보
    private String title;
    private String content;
    private boolean isResolved;

    // cnt 정보
    @Column(name = "view_cnt")
    private int viewCnt;
    @Column(name = "recommendation_cnt")
    private int recommendationCnt;

    // 참조 정보
    @Column(name = "questioner_id")
    private Long questionerId;
    @Column(name = "comm_id")
    private Long commId;

    public void updateQuestion(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateStatus(boolean isResolved) {
        this.isResolved = isResolved;
    }

}
