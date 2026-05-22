package org.example.apiratelimiter.response;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.example.apiratelimiter.algorithm.SlidingWindowAlgorithm;
import org.example.apiratelimiter.algorithm.TokenBucketAlgorithm;
import org.example.apiratelimiter.annotation.RateLimit;
import org.example.apiratelimiter.redis.RedisKeyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@AllArgsConstructor
public class AlgorithmExecuteEngine {

    private final TokenBucketAlgorithm tokenBucketAlgorithm;

    private final SlidingWindowAlgorithm
            slidingWindowAlgorithm;

    private final RedisKeyBuilder redisKeyBuilder;

    public boolean allowRequest(
            ServletRequestAttributes requestAttributes,
            RateLimit rateLimit
    ) {

        HttpServletRequest request =
                requestAttributes.getRequest();

        String customKey = rateLimit.key();

        String redisKey =
                redisKeyBuilder.generateKey(
                        request,
                        customKey
                );

        switch (rateLimit.algorithm()) {

            case SLIDING_WINDOW_COUNTER -> {

                int limit =
                        rateLimit.limit();

                int windowSize =
                        rateLimit.windowSize();

                return slidingWindowAlgorithm
                        .allowRequest(
                                redisKey,
                                limit,
                                windowSize
                        );
            }

            case TOKEN_BUCKET -> {

                int capacity =
                        rateLimit.capacity();

                int refillTokens =
                        rateLimit.refillTokens();

                int refillDuration =
                        rateLimit.refillDuration();

                return tokenBucketAlgorithm
                        .allowRequest(
                                redisKey,
                                capacity,
                                refillTokens,
                                refillDuration
                        );
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported algorithm: "
                            + rateLimit.algorithm()
            );
        }
    }
}