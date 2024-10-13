package com.hanshin.supernova.user.domain;

import jakarta.persistence.Column;

public class Activity {
    @Column(name = "marked_q_cnt")
    private int markedQuestionCnt;  // 멋진 질문자 배지 계수

    @Column(name = "popular_q_cnt")
    private int popularQuestionCnt; // 인기 질문자 배지 계수

    @Column(name = "accepted_a_cnt")
    private int acceptedAnswerCnt;  // 정확한 답변자 배지 계수

    @Column(name = "popular_a_cnt")
    private int popularAnswerCnt;   // 인기 답변자 배지 계수
}
