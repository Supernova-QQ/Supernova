package com.hanshin.supernova.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question_view")
public class QuestionView {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "viewer_id")
    private Long viewerId;

    public void updateViewedAt() {
        this.viewedAt = LocalDateTime.now();
    }
}
