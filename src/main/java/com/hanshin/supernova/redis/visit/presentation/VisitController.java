package com.hanshin.supernova.redis.visit.presentation;

import com.hanshin.supernova.popularity.dto.response.DailyVisitorCntResponse;
import com.hanshin.supernova.redis.visit.application.VisitService;
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
public class VisitController {

    private final VisitService visitService;

    @GetMapping("/{communityId}/visitors/daily")
    public ResponseEntity<DailyVisitorCntResponse> getDailyVisitorCount(
            @PathVariable(name = "communityId") Long communityId) {
        LocalDate targetDate = LocalDate.now();
        var response = visitService.getDailyVisitorCount(communityId, targetDate);
        return ResponseEntity.ok(response);
    }
}
