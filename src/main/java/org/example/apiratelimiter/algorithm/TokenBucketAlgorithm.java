package org.example.apiratelimiter.algorithm;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenBucketAlgorithm {

    private final StringRedisTemplate redisTemplate;

    /**
     * capacity              -> maximum bucket size
     * refillTokens          -> tokens added
     * refillDurationSeconds -> refill interval
     *
     * Example:
     * capacity = 100
     * refillTokens = 10
     * refillDurationSeconds = 1
     *
     * Means:
     * Add 10 tokens every second
     */

    public boolean allowRequest(
            String key,
            int capacity,
            int refillTokens,
            int refillDurationSeconds
    ) {

        long currentTime = System.currentTimeMillis();

        String tokensKey = key + ":tokens";
        String timestampKey = key + ":timestamp";

        String luaScript = """
                local tokensKey = KEYS[1]
                local timestampKey = KEYS[2]

                local capacity = tonumber(ARGV[1])
                local refillTokens = tonumber(ARGV[2])
                local refillDuration = tonumber(ARGV[3])
                local currentTime = tonumber(ARGV[4])

                local lastTokens = tonumber(redis.call("GET", tokensKey))
                if lastTokens == nil then
                    lastTokens = capacity
                end

                local lastRefillTime = tonumber(redis.call("GET", timestampKey))
                if lastRefillTime == nil then
                    lastRefillTime = currentTime
                end

                local elapsedTime = math.max(0, currentTime - lastRefillTime)

                local intervals = math.floor(
                    elapsedTime / (refillDuration * 1000)
                )

                local tokensToAdd = intervals * refillTokens

                local updatedTokens = math.min(
                    capacity,
                    lastTokens + tokensToAdd
                )

                local newRefillTime = lastRefillTime +
                    (intervals * refillDuration * 1000)

                local allowed = 0

                if updatedTokens >= 1 then
                    updatedTokens = updatedTokens - 1
                    allowed = 1
                end

                redis.call("SET", tokensKey, updatedTokens)
                redis.call("SET", timestampKey, newRefillTime)

                local ttl = refillDuration * 2

                redis.call("EXPIRE", tokensKey, ttl)
                redis.call("EXPIRE", timestampKey, ttl)

                return allowed
                """;

        DefaultRedisScript<Long> redisScript =
                new DefaultRedisScript<>();

        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        Long response = redisTemplate.execute(
                redisScript,
                List.of(tokensKey, timestampKey),
                String.valueOf(capacity),
                String.valueOf(refillTokens),
                String.valueOf(refillDurationSeconds),
                String.valueOf(currentTime)
        );
        return response == 1L;
    }
}
