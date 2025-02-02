package com.hanshin.supernova.question.infrastructure;

import com.hanshin.supernova.question.domain.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q " +
            "WHERE q.commId > 1" +
            "ORDER BY q.createdAt DESC " +
            "LIMIT :N")
    List<Question> findNLatestQuestionsByCreatedAtDesc(@Param("N") Integer limitNumber, Pageable pageable);

    @Query("SELECT q FROM Question q " +
            "WHERE q.commId = 1" +
            "ORDER BY q.createdAt DESC " +
            "LIMIT :N")
    List<Question> findNLatestQuestionsFromGeneralCommunityByCreatedAtDesc(@Param("N") Integer limitNumber, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.commId=:commId AND q.isResolved=:isResolved ORDER BY q.createdAt ASC")
    Page<Question> findAllByCommIdAndIsResolvedOrderByCreatedAtAsc(@Param("commId") Long c_id, @Param("isResolved") boolean isResolved, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.commId=:commId AND q.isResolved=:isResolved ORDER BY q.createdAt DESC")
    Page<Question> findAllByCommIdAndIsResolvedOrderByCreatedAtDesc(@Param("commId") Long c_id, @Param("isResolved") boolean isResolved, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.commId=:commId ORDER BY q.createdAt DESC")
    Page<Question> findAllByCommIdOrderByCreatedAtDesc(@Param("commId") Long c_id, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.commId=:commId ORDER BY q.createdAt ASC")
    Page<Question> findAllByCommIdOrderByCreatedAtAsc(@Param("commId") Long c_id, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.commId=:commId AND q.isResolved=:isResolved ORDER BY q.createdAt ASC")
    List<Question> findByIsResolvedOrderByCreatedAtDesc(@Param("commId") Long c_id, @Param("isResolved") boolean isResolved, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword% ORDER BY q.answerCnt DESC")
    Page<Question> searchByKeywordOrderByAnswerCount(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword% ORDER BY q.viewCnt DESC")
    Page<Question> searchByKeywordOrderByViewCount(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword% ORDER BY q.recommendationCnt DESC")
    Page<Question> searchByKeywordOrderByRecommendationCount(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword% ORDER BY q.createdAt DESC")
    Page<Question> searchByKeywordOrderByCreatedAt(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT q.id as questionId, COUNT(qv) as viewCount " +
            "FROM Question q " +
            "LEFT JOIN QuestionView qv ON q.id = qv.questionId " +
            "WHERE q.commId = :communityId " +
            "AND qv.viewedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY q.id " +
            "ORDER BY viewCount DESC " +
            "LIMIT :N")
    List<Object[]> findTopNPopularQuestionsByCommunityAndDate(
            @Param("communityId") Long communityId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("N") Integer limitNumber
    );

    @Query("SELECT q.id as questionId, COUNT(qv) as viewCount " +
            "FROM Question q " +
            "LEFT JOIN QuestionView qv ON q.id = qv.questionId " +
            "WHERE q.commId = :communityId " +
            "AND qv.viewedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY q.id " +
            "ORDER BY viewCount DESC")
    List<Object[]> findAllPopularQuestionsByCommunityAndDate(
            @Param("communityId") Long communityId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 특정 사용자의 질문 중 추천 수가 기준 이상인 질문 목록을 반환
    @Query("SELECT q.id FROM Question q WHERE q.questionerId = :userId AND q.recommendationCnt >= :recommendationThreshold")
    List<Long> findPopularQuestionsByUserId(@Param("userId") Long userId, @Param("recommendationThreshold") int recommendationThreshold);


    // 특정 사용자의 질문 중 북마크가 10회 이상 된 질문 목록을 반환
    @Query("SELECT q.id FROM Question q JOIN Bookmark b ON q.id = b.targetId " +
            "WHERE q.questionerId = :userId " +
            "GROUP BY q.id HAVING COUNT(b.id) >= :bookmarkThreshold")
    List<Long> findBookmarkedQuestionsByUserId(@Param("userId") Long userId,
                                               @Param("bookmarkThreshold") int bookmarkThreshold);

    // 특정 사용자가 작성한 질문 목록을 최신순으로 조회
    @Query("SELECT q FROM Question q WHERE q.questionerId = :userId ORDER BY q.createdAt DESC")
    List<Question> findAllByQuestionerId(@Param("userId") Long userId);

    // questionId로 commId 조회
    @Query("SELECT q.commId FROM Question q WHERE q.id = :questionId")
    Long findCommunityIdByQuestionId(@Param("questionId") Long questionId);
}
