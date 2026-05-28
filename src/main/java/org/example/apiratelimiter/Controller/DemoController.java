package org.example.apiratelimiter.Controller;

import org.example.apiratelimiter.annotation.RateLimit;
import org.example.apiratelimiter.enums.AlgorithmType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @RateLimit(algorithm = AlgorithmType.TOKEN_BUCKET, capacity = 5, refillTokens = 60,refillDuration = 1)
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    @RateLimit(algorithm = AlgorithmType.TOKEN_BUCKET,capacity = 6, refillTokens = 5, refillDuration = 1)
    @GetMapping("/middleware-check")
    public String testMiddleware() {
        return "Middleware-level rate limit passed!";
    }

    // Corrected endpoint: Sliding Window, with a specific key, 7 req/30sec window
    // All requests using this key will share the same rate limit bucket
    @RateLimit(algorithm = AlgorithmType.SLIDING_WINDOW_COUNTER, limit = 7, windowSize = 1, key = "specific-sw-key") // <--- FIX IS HERE
    @GetMapping("/sliding-window-with-key")
    public String slidingWindowWithKey() {
        return "Sliding Window (with-key) endpoint!";
    }
}
