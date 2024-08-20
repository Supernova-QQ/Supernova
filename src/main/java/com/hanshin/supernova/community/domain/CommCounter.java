package com.hanshin.supernova.community.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommCounter {

    @Column(name = "member_cnt")
    private int memberCnt;

    @Column(name = "question_cnt")
    private int questionCnt;

    @Column(name = "visitor_cnt")
    private int visitorCnt;

    public void increaseMemberCnt() {
        this.memberCnt++;
    }
    public void decreaseMemberCnt() {
        this.memberCnt--;
        // TODO memberCnt 가 0 이하로 됐을 때의 예외처리
    }

//    public void increaseQuestionCnt() {
//        this.questionCnt++;
//    }
//
//    public void increaseVisitorCnt() {
//        this.visitorCnt++;
//    }
}
