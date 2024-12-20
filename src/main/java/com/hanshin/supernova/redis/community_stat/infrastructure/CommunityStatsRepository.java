package com.hanshin.supernova.redis.community_stat.infrastructure;

import com.hanshin.supernova.redis.community_stat.domain.CommunityStats;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityStatsRepository extends JpaRepository<CommunityStats, Long> {

    boolean existsByVisitorIdentifierAndDateAndCommunityId(String visitorIdentifier, LocalDate date, Long communityId);

    /**
     * 일정 기간 동안 방문자 수가 가장 많은 순서대로 3개 커뮤니티 조회
     * @param startDate 시작 날짜(현재로부터 30일 전)
     * @param endDate   종료 날짜(현재)
     * @return          커뮤니티 리스트
     */
    @Query("SELECT cs.communityId, COUNT(DISTINCT cs.visitorIdentifier) as visitorCnt " +
            "FROM CommunityStats cs " +
            "WHERE cs.date BETWEEN :startDate AND :endDate " +
            "AND cs.communityId > 1" +
            "GROUP BY cs.communityId " +
            "ORDER BY visitorCnt DESC " +
            "LIMIT :n")
    List<Object[]> findTopNCommunitiesByVisitorsInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("n") int n);

    /**
     * 특정 커뮤니티의 당일 방문자수 조회
     * @param date          방문자 수를 알고 싶은 날짜 정보(현재)
     * @return              방문자 수 반환
     */
    @Query("SELECT COUNT(DISTINCT v.visitorIdentifier) FROM CommunityStats v " +
            "WHERE v.communityId = :communityId AND v.date = :date")
    Long countDistinctVisitorsByCommunityIdAndDate(
            @Param("communityId") Long communityId,
            @Param("date") LocalDate date);
}
