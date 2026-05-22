package org.example.apiratelimiter.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.apiratelimiter.annotation.RateLimit;
import org.example.apiratelimiter.exception.RateLimitExceededException;
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

        boolean allowed =
                algorithmExecuteEngine.allowRequest(
                        requestAttributes,
                        rateLimit
                );

        if (!allowed) {
            throw new RateLimitExceededException(
                    "Too many requests"
            );
        }

        return joinPoint.proceed();
    }
}