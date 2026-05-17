package org.example.apiratelimiter.Controller;

import org.example.apiratelimiter.annotation.RateLimit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @RateLimit(limit = 5, windowSize = 60)
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }
}
