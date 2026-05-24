package org.example.apiratelimiter.configuration;

import org.example.apiratelimiter.enums.AlgorithmType;

public record RateLimitConfig(

        AlgorithmType algorithm,

        int limit,
        int windowSize,

        int capacity,
        int refillTokens,
        int refillDuration,
        int leakRate
) {
}