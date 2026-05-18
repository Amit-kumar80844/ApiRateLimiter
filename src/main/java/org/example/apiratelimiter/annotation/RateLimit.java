package org.example.apiratelimiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [limit] is limit for each user if key is not given
 * else for a features where it is used
 * [windowSize] is size of window for algo
 * [key] is for custom url inplace so that a same type of
 * features shojld have same limit
* */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int limit();
    int windowSize();
    String key() default "";
}

