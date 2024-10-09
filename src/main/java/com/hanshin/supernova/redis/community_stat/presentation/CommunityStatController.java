package com.hanshin.supernova.redis.community_stat.presentation;

import com.hanshin.supernova.popularity.dto.response.DailyVisitorCntResponse;
import com.hanshin.supernova.redis.community_stat.application.CommunityStatService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/communities")
@RequiredArgsConstructor
public class CommunityStatController {

    private final CommunityStatService communityStatService;

    @GetMapping("/{communityId}/visitors/daily")
    public ResponseEntity<DailyVisitorCntResponse> getDailyVisitorCount(
            @PathVariable(name = "communityId") Long communityId) {
        LocalDate targetDate = LocalDate.now();
        var response = communityStatService.getDailyVisitorCount(communityId, targetDate);
        return ResponseEntity.ok(response);
    }
}
