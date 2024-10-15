package com.hanshin.supernova.popularity.dto.response;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyVisitorCntResponse {

    private Long communityId;
    private String communityName;
    private LocalDate date;
    private Long visitorCount;
}
