package org.example.apiratelimiter.algorithm;

public class TokenBucketAlgorithm implements RateLimitAlgorithm{
    @Override
    public boolean allowRequest(String Id) {
        return true;
    }
}
