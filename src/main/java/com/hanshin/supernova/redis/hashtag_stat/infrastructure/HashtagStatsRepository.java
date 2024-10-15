package com.hanshin.supernova.redis.hashtag_stat.infrastructure;

import com.hanshin.supernova.redis.hashtag_stat.domain.HashtagStats;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HashtagStatsRepository extends JpaRepository<HashtagStats, String> {

    @Query("SELECT hs.hashtagId, COUNT(*) AS tagCnt "
            + "FROM HashtagStats hs "
            + "WHERE hs.date = :yesterday "
            + "GROUP BY hs.hashtagId "
            + "ORDER BY tagCnt DESC "
            + "LIMIT 10")
    List<Object[]> findTop10HashtagsByTaggersInDateRange(@Param("yesterday") LocalDate yesterday);

}
