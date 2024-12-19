package com.hanshin.supernova.rate_limiter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateLimitStatusResponse {

    private long remainingGenerateCount;
}
