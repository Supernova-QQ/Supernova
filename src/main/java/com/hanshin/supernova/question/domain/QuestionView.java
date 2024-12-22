package com.hanshin.supernova.question.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "viewed_at")
    private LocalDate viewedAt;

    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "visitor_identifier")
    private String visitorIdentifier;   // IP 주소

    @Column(name = "comm_id")
    private Long commId;
}
