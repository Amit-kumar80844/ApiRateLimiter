package org.example.apiratelimiter.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.apiratelimiter.annotation.RateLimit;
import org.example.apiratelimiter.configuration.RateLimitConfig;
import org.example.apiratelimiter.exception.RateLimitExceededException;
import org.example.apiratelimiter.redis.RedisKeyBuilder;
import org.example.apiratelimiter.response.AlgorithmExecuteEngine;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final AlgorithmExecuteEngine
            algorithmExecuteEngine;
    private final RedisKeyBuilder redisKeyBuilder;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(
            ProceedingJoinPoint joinPoint,
            RateLimit rateLimit
    ) throws Throwable {

        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes)
                        RequestContextHolder
                                .getRequestAttributes();

        if (requestAttributes == null) {

            throw new IllegalStateException(
                    "No RequestAttributes found"
            );
        }

        HttpServletRequest request =
                requestAttributes.getRequest();

        String redisKey =
                redisKeyBuilder.generateKey(
                        request,
                        rateLimit.key()
                );

        RateLimitConfig config =
                new RateLimitConfig(
                        rateLimit.algorithm(),

                        rateLimit.limit(),
                        rateLimit.windowSize(),

                        rateLimit.capacity(),
                        rateLimit.refillTokens(),
                        rateLimit.refillDuration(),
                        rateLimit.leakRate()
                );

        boolean allowed =
                algorithmExecuteEngine.allowRequest(
                        redisKey,
                        config
                );

        if (!allowed) {
            throw new RateLimitExceededException(
                    "Too many requests"
            );
        }

        return joinPoint.proceed();
    }
}