package com.hanshin.supernova.answer.infrastructure;

import com.hanshin.supernova.answer.domain.Answer;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByQuestionId(Long qId);

    /**
     * 특정 사용자가 작성한 답변 중 소스가 있고 채택된 답변을 가져오는 메서드
     *
     * @param userId 답변 작성자 ID
     * @return 소스가 있고 채택된 답변 목록
     */
    @Query("SELECT a FROM Answer a WHERE a.answererId = :userId AND a.isAccepted = true AND a.source IS NOT NULL")
    List<Answer> findAcceptedAnswersWithSourceByUserId(@Param("userId") Long userId);

    /**
     * 사용자가 작성한 답변 중 소스를 제공하고 추천 수가 10회 이상인 답변 목록을 조회합니다.
     *
     * @param userId                  사용자의 ID
     * @param recommendationThreshold 추천 수 기준
     * @return 추천 수가 기준을 넘는 답변 목록
     */
    @Query("SELECT a FROM Answer a " +
            "WHERE a.answererId = :userId AND a.source IS NOT NULL " +
            "AND a.recommendationCnt >= :recommendationThreshold")
    List<Answer> findPopularAnswersWithSourceByUserId(@Param("userId") Long userId,
                                                      @Param("recommendationThreshold") int recommendationThreshold);


    // 특정 사용자가 작성한 답변 목록을 최신순으로 조회
    @Query("SELECT a FROM Answer a WHERE a.answererId = :userId ORDER BY a.createdAt DESC")
    List<Answer> findAllByAnswererId(@Param("userId") Long userId);
}
