package com.hanshin.supernova.question.domain;

import com.hanshin.supernova.common.entity.BaseEntity;
import jakarta.persistence.Column;
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
@Table(name = "question")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
//    @Column(name = "answer_cnt")
//    private Long answerCnt;

    // 참조 정보
    @Column(name = "questioner_id")
    private Long questionerId;
    @Column(name = "comm_id")
    private Long commId;

    public void updateQuestion(String title, String content, Long commId) {
        this.title = title;
        this.content = content;
        this.commId = commId;
    }

    public void changeStatus() {
        this.isResolved = !isResolved;
    }

    public void updateViewCnt() {
        this.viewCnt++;
    }

    public void updateRecommendationCnt() {
        this.recommendationCnt++;
    }

//    public void increaseAnswerCnt() {
//        this.answerCnt++;
//    }
//
//    public void decreaseAnswerCnt() {
//        this.answerCnt--;
//    }   // TODO 답변 수가 0 아래로 내려갈 때 예외

}
