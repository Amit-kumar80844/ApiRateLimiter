package org.example.apiratelimiter.algorithm;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SlidingWindowAlgorithm {

    private final StringRedisTemplate redisTemplate;

    @Value("${rate-limiter.sliding-window.ttl-seconds}")
    private long ttlSeconds;

    private static final String LUA_SCRIPT = """
            
            local currentWindowKey = KEYS[1]
            local previousWindowKey = KEYS[2]

            local limit = tonumber(ARGV[1])
            local windowSize = tonumber(ARGV[2])
            local currentTime = tonumber(ARGV[3])
            local ttl = tonumber(ARGV[4])

            local currentWindowCount =
                tonumber(redis.call("GET", currentWindowKey))

            if currentWindowCount == nil then
                currentWindowCount = 0
            end

            local previousWindowCount =
                tonumber(redis.call("GET", previousWindowKey))

            if previousWindowCount == nil then
                previousWindowCount = 0
            end

            local elapsedTime =
                currentTime % windowSize

            local weight =
                (windowSize - elapsedTime) / windowSize

            local estimatedCount =
                currentWindowCount +
                (previousWindowCount * weight)

            if estimatedCount >= limit then
                return 0
            end

            local updatedCount =
                redis.call("INCR", currentWindowKey)

            redis.call(
                "EXPIRE",
                currentWindowKey,
                ttl
            )

            return 1
            """;

    private final DefaultRedisScript<Long> redisScript =
            createRedisScript();

    private DefaultRedisScript<Long> createRedisScript() {

        DefaultRedisScript<Long> script =
                new DefaultRedisScript<>();

        script.setScriptText(LUA_SCRIPT);
        script.setResultType(Long.class);

        return script;
    }

    /**
     * limit      -> max requests allowed
     * windowSize -> sliding window size in seconds
     */

    public boolean allowRequest(
            String key,
            int limit,
            int windowSize
    ) {

        long currentTime =
                System.currentTimeMillis() / 1000;

        long currentWindow =
                currentTime / windowSize;

        long previousWindow =
                currentWindow - 1;

        String currentWindowKey =
                key + ":" + currentWindow;

        String previousWindowKey =
                key + ":" + previousWindow;

        Long response = redisTemplate.execute(
                redisScript,
                List.of(
                        currentWindowKey,
                        previousWindowKey
                ),
                String.valueOf(limit),
                String.valueOf(windowSize),
                String.valueOf(currentTime),
                String.valueOf(ttlSeconds)
        );

        return response != null && response == 1L;
    }
}