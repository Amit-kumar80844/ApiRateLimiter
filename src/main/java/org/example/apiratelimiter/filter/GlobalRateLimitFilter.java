package org.example.apiratelimiter.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.apiratelimiter.enums.AlgorithmType;
import org.example.apiratelimiter.redis.RedisKeyBuilder;
import org.example.apiratelimiter.response.AlgorithmExecuteEngine;
import org.example.apiratelimiter.configuration.RateLimitConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class GlobalRateLimitFilter
        extends OncePerRequestFilter {

    private final AlgorithmExecuteEngine engine;

    private final RedisKeyBuilder redisKeyBuilder;

    @Value("${global-rate-limit.capacity:100}")
    private int capacity;

    @Value("${global-rate-limit.refill-rate:100}")
    private int refillRate;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String redisKey =
                redisKeyBuilder.generateKey(
                        request,
                        "GLOBAL"
                );

        RateLimitConfig config =
                new RateLimitConfig(
                        AlgorithmType.TOKEN_BUCKET,
                        0,
                        0,
                        capacity,
                        refillRate,
                        60,
                        0
                );

        boolean allowed =
                engine.allowRequest(
                        redisKey,
                        config
                );

        if (!allowed) {

            response.setStatus(429);
            response.getWriter()
                    .write("Too Many Requests");
            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}