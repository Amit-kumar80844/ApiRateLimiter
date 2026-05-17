package org.example.apiratelimiter.aspect;

import org.example.apiratelimiter.algorithm.TokenBucketAlgorithm;
import org.example.apiratelimiter.annotation.RateLimit;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.apiratelimiter.constants.RedisKeys;
import org.example.apiratelimiter.exception.RateLimitExceededException;
import org.example.apiratelimiter.extractor.JwtIdentityExtractor;
import org.example.apiratelimiter.extractor.UriExtractor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class RateLimitAspect {

    @Around("@annotation(rateLimit)")
    public Object rateLimit(
            ProceedingJoinPoint joinPoint,
            RateLimit rateLimit
    ) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new IllegalStateException("No RequestAttributes found. This aspect should only be used in a web context.");
        }
        HttpServletRequest request = requestAttributes.getRequest();/* [request] have headers,ip,jwt,uri */
        RedisKeys redisKeys = new RedisKeys();// will replace it
        TokenBucketAlgorithm tokenBucketAlgorithm = new TokenBucketAlgorithm();

        JwtIdentityExtractor jwtIdentityExtractor = new JwtIdentityExtractor();// use autowire
        UriExtractor uriExtractor = new UriExtractor();
        String userId = jwtIdentityExtractor.extractIdentity(request);
        String uri = uriExtractor.extract(request);
         // exception ends now work on redis
        String key = redisKeys.generateKey(uri,userId);
        if (!(tokenBucketAlgorithm.allowRequest(key))) {
            throw new RateLimitExceededException(
                    "To many request"
            );
        }
        return joinPoint.proceed();
    }
}
