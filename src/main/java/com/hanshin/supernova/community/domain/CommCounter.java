package com.hanshin.supernova.community.domain;

import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
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

    public void increaseMemberCnt() {
        this.memberCnt++;
    }
    public void decreaseMemberCnt() {
        if (this.memberCnt > 0) {
            this.memberCnt--;
        } else {
            throw new CommunityInvalidException(ErrorType.CNT_NEGATIVE_ERROR);
        }
    }

    public void increaseQuestionCnt() {
        this.questionCnt++;
    }
    public void decreaseQuestionCnt() {
        if (this.questionCnt > 0) {
            this.questionCnt--;
        } else {
            throw new CommunityInvalidException(ErrorType.CNT_NEGATIVE_ERROR);
        }
    }
}
