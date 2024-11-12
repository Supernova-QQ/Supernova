package com.hanshin.supernova.redis.community_stat.application;

import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.popularity.dto.response.DailyVisitorCntResponse;
import com.hanshin.supernova.redis.community_stat.infrastructure.CommunityStatsRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityStatService {

    private final CommunityRepository communityRepository;
    private final CommunityStatsRepository communityStatsRepository;

    @Transactional(readOnly = true)
    public DailyVisitorCntResponse getDailyVisitorCount(Long communityId, LocalDate date) {
        String communityName = communityRepository.findById(communityId)
                .map(Community::getName)
                .orElseThrow(() -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR));

        Long visitorCount = communityStatsRepository.countDistinctVisitorsByCommunityIdAndDate(communityId, date);

        return DailyVisitorCntResponse.builder()
                .communityId(communityId)
                .communityName(communityName)
                .date(date)
                .visitorCount(visitorCount)
                .build();
    }
}
