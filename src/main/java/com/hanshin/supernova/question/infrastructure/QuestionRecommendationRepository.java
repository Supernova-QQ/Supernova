package com.hanshin.supernova.question.infrastructure;

import com.hanshin.supernova.question.domain.QuestionRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRecommendationRepository extends
        JpaRepository<QuestionRecommendation, Long> {
    QuestionRecommendation findByQuestionIdAndRecommenderId(Long questionId, Long recommenderId);
}
