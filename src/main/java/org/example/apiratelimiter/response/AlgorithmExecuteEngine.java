package org.example.apiratelimiter.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.example.apiratelimiter.algorithm.SlidingWindowAlgorithm;
import org.example.apiratelimiter.algorithm.TokenBucketAlgorithm;
import org.example.apiratelimiter.annotation.RateLimit;
import org.example.apiratelimiter.configuration.RateLimitConfig;
import org.example.apiratelimiter.redis.RedisKeyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.example.apiratelimiter.enums.AlgorithmType.SLIDING_WINDOW_COUNTER;

@Component
@AllArgsConstructor
public class AlgorithmExecuteEngine {

    private final TokenBucketAlgorithm tokenBucketAlgorithm;

    private final SlidingWindowAlgorithm
            slidingWindowAlgorithm;

    public boolean allowRequest(
            String redisKey,
            RateLimitConfig config
    ) {

        switch (config.algorithm()) {

            case SLIDING_WINDOW_COUNTER -> {

                return slidingWindowAlgorithm
                        .allowRequest(
                                redisKey,
                                config.limit(),
                                config.windowSize()
                        );
            }

            case TOKEN_BUCKET -> {

                return tokenBucketAlgorithm
                        .allowRequest(
                                redisKey,
                                config.capacity(),
                                config.refillTokens(),
                                config.refillDuration()
                        );
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported algorithm"
            );
        }
    }
}