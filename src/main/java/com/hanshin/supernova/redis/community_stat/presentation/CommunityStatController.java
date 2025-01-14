package com.hanshin.supernova.redis.community_stat.presentation;

import com.hanshin.supernova.popularity.dto.response.DailyVisitorCntResponse;
import com.hanshin.supernova.redis.community_stat.application.CommunityStatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "커뮤니티 일일 방문자수 조회", description = "방문자의 ip 주소를 바탕으로 집계한 일일 방문자수 조회")
    @GetMapping("/{communityId}/visitors/daily")
    public ResponseEntity<DailyVisitorCntResponse> getDailyVisitorCount(
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "communityId") Long communityId) {
        LocalDate targetDate = LocalDate.now();
        var response = communityStatService.getDailyVisitorCount(communityId, targetDate);
        return ResponseEntity.ok(response);
    }
}
