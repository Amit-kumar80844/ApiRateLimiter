package org.example.apiratelimiter.annotation;

import org.example.apiratelimiter.enums.AlgorithmType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [capacity] is limit for each user if key is not given
 * else for a features where it is used
 * [refillTokens] is size of window for algo
 * [key] is for custom url inplace so that a same type of
 * [refillDuration] time in sec to refill for users
 * features should have same limit
 *<p>
 * here custom key can be used to not
 * use uri else will uri automatically
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    AlgorithmType algorithm();
    int limit() default 0;
    int windowSize() default 0;
    int capacity() default 0;
    int refillTokens() default 0;
    int refillDuration() default 0;
    int leakRate() default 0;
    String key() default "";
}

