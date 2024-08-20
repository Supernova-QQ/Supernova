package com.hanshin.supernova.community.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class CommCounter {

    @Column(name = "member_cnt")
    private int memberCnt;

    @Column(name = "question_cnt")
    private int questionCnt;

    @Column(name = "visitor_cnt")
    private int visitorCnt;
}
