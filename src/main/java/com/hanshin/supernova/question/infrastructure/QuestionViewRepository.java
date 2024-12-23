package com.hanshin.supernova.question.infrastructure;

import com.hanshin.supernova.question.domain.QuestionView;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface QuestionViewRepository extends JpaRepository<QuestionView, Long> {

    @Query("SELECT qv.questionId, COUNT(DISTINCT qv.visitorIdentifier) AS viewCnt "
            + "FROM QuestionView qv "
            + "WHERE qv.viewedAt BETWEEN :startDate AND :endDate "
            + "AND qv.commId > 1"
            + "GROUP BY qv.questionId "
            + "ORDER BY viewCnt DESC "
            + "LIMIT :N")
    List<Object[]> findTopNQuestionsByViewCntInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("N") Integer limitNumber);

    boolean existsByVisitorIdentifierAndViewedAtAndQuestionId(String visitorIdentifier, LocalDate date,
            Long questionId);
}
