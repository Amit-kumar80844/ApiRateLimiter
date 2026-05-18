package org.example.apiratelimiter.algorithm;

import lombok.NonNull;

public class TokenBucketAlgorithm implements RateLimitAlgorithm{
    @Override
    public boolean allowRequest(@NonNull String redisKey,@NonNull int limit,@NonNull int window) {
        return true;
    }
}
