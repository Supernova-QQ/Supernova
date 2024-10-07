package com.hanshin.supernova.redis.visit.infrastructure;

import com.hanshin.supernova.redis.visit.domain.Visitor;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    boolean existsByVisitorIdentifierAndDateAndCommunityId(String visitorIdentifier, LocalDate date, Long communityId);

    /**
     * 일정 기간 동안 방문자 수가 가장 많은 순서대로 3개 커뮤니티 조회
     * @param startDate 시작 날짜(현재로부터 30일 전)
     * @param endDate   종료 날짜(현재)
     * @return          커뮤니티 리스트
     */
    @Query("SELECT v.communityId, COUNT(DISTINCT v.visitorIdentifier) as visitorCount " +
            "FROM Visitor v " +
            "WHERE v.date BETWEEN :startDate AND :endDate " +
            "GROUP BY v.communityId " +
            "ORDER BY visitorCount DESC " +
            "LIMIT 3")
    List<Object[]> findTop3CommunitiesByVisitorsInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 특정 커뮤니티의 당일 방문자수 조회
     * @param date          방문자 수를 알고 싶은 날짜 정보(현재)
     * @return              방문자 수 반환
     */
    @Query("SELECT COUNT(DISTINCT v.visitorIdentifier) FROM Visitor v " +
            "WHERE v.communityId = :communityId AND v.date = :date")
    Long countDistinctVisitorsByCommunityIdAndDate(
            @Param("communityId") Long communityId,
            @Param("date") LocalDate date);
}
