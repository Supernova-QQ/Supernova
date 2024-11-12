package com.hanshin.supernova.answer.infrastructure;

import com.hanshin.supernova.answer.domain.AnswerRecommendation;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRecommendationRepository extends JpaRepository<AnswerRecommendation, Long> {
    AnswerRecommendation findByAnswerIdAndRecommenderId(Long answerId, Long recommenderId);
    @Query("SELECT ar.answerId, COUNT(DISTINCT ar.recommenderId) AS recommendCnt "
            + "FROM AnswerRecommendation ar "
            + "WHERE ar.recommendedAt BETWEEN :startDate AND :endDate "
            + "GROUP BY ar.answerId "
            + "ORDER BY recommendCnt DESC "
            + "LIMIT :N")
    List<Object[]> findTopNAnswersByRecommendationCntInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("N") Integer limitNumber);
}
