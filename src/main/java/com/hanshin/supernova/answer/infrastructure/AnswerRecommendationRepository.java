package com.hanshin.supernova.answer.infrastructure;

import com.hanshin.supernova.answer.domain.AnswerRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRecommendationRepository extends JpaRepository<AnswerRecommendation, Long> {
    AnswerRecommendation findByAnswerIdAndRecommenderId(Long answerId, Long recommenderId);
}
