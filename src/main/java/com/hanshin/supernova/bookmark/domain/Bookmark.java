package com.hanshin.supernova.bookmark.domain;

import com.hanshin.supernova.exception.bookmark.BookmarkAssignedException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.answer.domain.Answer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 생성 시 유효성 검사 및 현재 시간을 설정하는 메서드
    @PrePersist
    public void validateBookmark() {

        // 유효성 검사: question 또는 answer 중 하나만 존재해야 함
        if ((question == null && answer == null) || (question != null && answer != null)) {
            throw new BookmarkAssignedException(ErrorType.ASSIGNED_BOOKMARK_ERROR);
        }

        // 현재 시간을 생성 시간으로 설정
        this.createdAt = LocalDateTime.now();
    }
}