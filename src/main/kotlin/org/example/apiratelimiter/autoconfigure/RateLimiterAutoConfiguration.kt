package org.example.apiratelimiter.autoconfigure

import org.example.apiratelimiter.filter.GlobalRateLimitFilter
import org.example.apiratelimiter.response.AlgorithmExecuteEngine
import org.example.apiratelimiter.redis.RedisKeyBuilder
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean

@AutoConfiguration
open class RateLimiterAutoConfiguration {

    @Bean
    open fun globalRateLimitFilter(
        engine: AlgorithmExecuteEngine,
        redisKeyBuilder: RedisKeyBuilder
    ): GlobalRateLimitFilter {
        return GlobalRateLimitFilter(
            engine,
            redisKeyBuilder
        )
    }
}
