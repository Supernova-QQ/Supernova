package com.hanshin.supernova.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Embeddable
@Data
@RequiredArgsConstructor
public class Activity {

    @Column(name = "marked_q_badge")
    private boolean markedQuestionBadge;  // 멋진 질문자 배지 상태

    @Column(name = "popular_q_badge")
    private boolean popularQuestionBadge; // 인기 질문자 배지 상태

    @Column(name = "accepted_a_badge")
    private boolean acceptedAnswerBadge;  // 정확한 답변자 배지 상태

    @Column(name = "popular_a_badge")
    private boolean popularAnswerBadge;   // 인기 답변자 배지 상태


    // buildAndSaveUser 메서드 실행 시 네 개의 배지 상태가 모두 false로 설정되어 저장됨
    public Activity(boolean markedQuestionBadge, boolean popularQuestionBadge, boolean acceptedAnswerBadge, boolean popularAnswerBadge) {
        this.markedQuestionBadge = markedQuestionBadge;
        this.popularQuestionBadge = popularQuestionBadge;
        this.acceptedAnswerBadge = acceptedAnswerBadge;
        this.popularAnswerBadge = popularAnswerBadge;
    }

    /**
     * 멋진 질문자 배지 여부를 반환
     * @return true if the badge is acquired, false otherwise
     */
    public boolean hasMarkedQuestionBadge() {
        return markedQuestionBadge;
    }

    /**
     * 멋진 질문자 배지를 설정
     * @param markedQuestionBadgeStatus true로 설정하면 배지를 획득한 것으로 간주
     */
    public void setMarkedQuestionBadge(boolean markedQuestionBadgeStatus) {
        this.markedQuestionBadge = markedQuestionBadgeStatus;
    }

    /**
     * 인기 질문자 배지 여부를 반환
     * @return true if the badge is acquired, false otherwise
     */
    public boolean hasPopularQuestionBadge() {
        return popularQuestionBadge;
    }

    /**
     * 인기 질문자 배지를 설정
     * @param popularQuestionBadgeStatus true로 설정하면 배지를 획득한 것으로 간주
     */
    public void setPopularQuestionBadge(boolean popularQuestionBadgeStatus) {
        this.popularQuestionBadge = popularQuestionBadgeStatus;
    }

    /**
     * 정확한 답변자 배지 여부를 반환
     * @return true if the badge is acquired, false otherwise
     */
    public boolean hasAcceptedAnswerBadge() {
        return acceptedAnswerBadge;
    }

    /**
     * 정확한 답변자 배지를 설정
     * @param acceptedAnswerBadgeStatus true로 설정하면 배지를 획득한 것으로 간주
     */
    public void setAcceptedAnswerBadge(boolean acceptedAnswerBadgeStatus) {
        this.acceptedAnswerBadge = acceptedAnswerBadgeStatus;
    }

    /**
     * 인기 답변자 배지 여부를 반환
     * @return true if the badge is acquired, false otherwise
     */
    public boolean hasPopularAnswerBadge() {
        return popularAnswerBadge;
    }

    /**
     * 인기 답변자 배지를 설정
     * @param popularAnswerBadgeStatus true로 설정하면 배지를 획득한 것으로 간주
     */
    public void setPopularAnswerBadge(boolean popularAnswerBadgeStatus) {
        this.popularAnswerBadge = popularAnswerBadgeStatus;
    }
}
