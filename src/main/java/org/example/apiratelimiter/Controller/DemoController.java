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

    @RateLimit(algorithm = AlgorithmType.TOKEN_BUCKET,capacity = 6, refillTokens = 5, refillDuration = 10)
    @GetMapping("/middleware-check")
    public String testMiddleware() {
        return "Middleware-level rate limit passed!";
    }
}
