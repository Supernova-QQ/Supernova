package com.hanshin.supernova.answer.domain;

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
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String answer;

    @Enumerated
    private Tag tag;

    private String source;

    @Column(name = "is_ai", updatable = false)
    private boolean isAi;

    @Column(name = "is_accepted")
    private boolean isAccepted;

    @Column(name = "recommendation_cnt")
    private int recommendationCnt;

    @Column(name = "answerer_id")
    private Long answererId;

    @Column(name = "question_id")
    private Long questionId;

    public void updateAnswer(String answer, Tag tag, String source) {
        this.answer = answer;
        this.tag = tag;
        this.source = source;
    }

    public void changeStatus() {
        this.isAccepted = !isAccepted;
    }

    public void increaseRecommendationCnt() {
        this.recommendationCnt++;
    }

    public void decreaseRecommendationCnt() {
        this.recommendationCnt--;
    }
}
