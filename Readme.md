# API Rate Limiter SDK

## Overview

The API Rate Limiter SDK is a robust and flexible solution designed to protect your Spring Boot applications from abuse, ensure fair resource usage, and maintain high availability by controlling the rate at which clients can access your APIs. Built as a Spring Boot Auto-Configuration, it seamlessly integrates into your existing projects, providing an essential layer of defense against malicious attacks, accidental overuse, and resource exhaustion.

## What is an API Rate Limiter?

An API Rate Limiter is a mechanism that restricts the number of requests a user or client can make to an API within a given timeframe. Without rate limiting, a single client could flood your servers with requests, leading to denial-of-service (DoS) attacks, degraded performance for other users, or excessive infrastructure costs.

## What Can This SDK Do?

This SDK provides a powerful, configurable, and easy-to-integrate rate limiting solution for your Spring Boot applications.

### Key Features:

*   **Prevent Abuse & DoS Attacks**: Safeguard your backend services from being overwhelmed by a single client or malicious actors.
*   **Ensure Fair Usage**: Distribute API access equitably among all consumers, preventing one user from monopolizing resources.
*   **Resource Protection**: Protect your database, external services, and computational resources from excessive load.
*   **Seamless Spring Boot Integration**: Leverages Spring Boot's auto-configuration capabilities for minimal setup. Just add the dependency, and it's ready to go.
*   **Redis-Backed Persistence**: Utilizes Redis for efficient, distributed, and scalable rate limit tracking across multiple instances of your application.
*   **Configurable Algorithms**: Supports various rate limiting algorithms (e.g., Token Bucket, Sliding Window) to suit different use cases.
*   **Customizable Key Generation**: Define how client identifiers (e.g., IP address, API key, user ID) are extracted to apply rate limits.
*   **Extensible Design**: Designed to be easily extended and customized to meet specific application requirements.

## How to Use It (Terms of Use & Integration)

### 1. Add as a Dependency

To use this SDK, include it as a dependency in your Spring Boot project's `pom.xml` (for Maven) or `build.gradle` (for Gradle).

```xml
<!-- Maven Example -->
<dependency>
    <groupId>org.example</groupId> <!-- Replace with your actual groupId -->
    <artifactId>api-rate-limiter-sdk</artifactId> <!-- Replace with your actual artifactId -->
    <version>1.0.0</version> <!-- Use the latest version -->
</dependency>
```

### 2. Configure Redis

This SDK relies on Redis for storing rate limiting counters. Ensure you have a running Redis instance and configure its connection details in your `application.properties` or `application.yml`:

```properties
# application.properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
# spring.data.redis.password=yourpassword (if applicable)
# spring.data.redis.database=0 (if applicable)
```

### 3. Automatic Configuration

Thanks to Spring Boot's `@AutoConfiguration`, the `GlobalRateLimitFilter` will be automatically registered as a Spring Bean and applied to your application's request pipeline. You typically don't need to write any additional Java/Kotlin code to enable the basic rate limiting functionality.

### 4. Customization and Advanced Usage

The SDK provides a powerful `@RateLimit` annotation that allows you to define specific rate limiting rules directly on your controller methods.

*   **Endpoint-Specific Rate Limits**: Apply different rate limits to different API endpoints.
*   **Algorithm Selection**: Choose between `TOKEN_BUCKET` (for a smooth rate with burst capacity) or `SLIDING_WINDOW` (for a more accurate count over a rolling time window).
*   **Key-Based Limiting**:
    *   If you provide a `key` parameter in the `@RateLimit` annotation, the rate limit will be applied specifically to that key. This is useful for limiting based on an API key, user ID, or any custom identifier you extract.
    *   If the `key` parameter is omitted or `null`, the rate limit will default to a "per client" basis, typically identified by the client's IP address or a default request identifier.

**`@RateLimit` Annotation Parameters:**

*   `algorithm`: (`AlgorithmType.TOKEN_BUCKET` or `AlgorithmType.SLIDING_WINDOW`) - Specifies the rate limiting algorithm to use.
*   `capacity`: (int) - The maximum number of requests allowed within the defined period (for `SLIDING_WINDOW`) or the bucket size (for `TOKEN_BUCKET`).
*   `refillTokens`: (int) - (Only for `TOKEN_BUCKET`) The number of tokens added to the bucket at each refill interval.
*   `refillDuration`: (int) - (Only for `TOKEN_BUCKET`) The duration in seconds after which `refillTokens` are added to the bucket.
*   `key`: (String, optional) - A custom key to identify the client for rate limiting. If not provided, a default client identifier (e.g., IP address) will be used.

### 5. Terms of Use

This SDK is provided under the [MIT License / Apache 2.0 License / Your Chosen License]. By using this SDK, you agree to the terms specified in the LICENSE file. It is intended to be used as a tool to enhance the resilience and security of your applications. While designed for robustness, it is crucial to perform thorough testing in your specific environment. The authors and contributors are not liable for any damages or issues arising from its use.

## Getting Started Example

Here's how you can apply the `@RateLimit` annotation to your controller methods:

```java
package org.example.apiratelimiter.Controller;

import org.example.apiratelimiter.annotation.RateLimit;
import org.example.apiratelimiter.enums.AlgorithmType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    // This endpoint uses the Token Bucket algorithm:
    // Allows 5 requests initially, then refills 60 tokens every 1 second.
    // The rate limit is applied globally or per default client identifier.
    @RateLimit(algorithm = AlgorithmType.TOKEN_BUCKET, capacity = 5, refillTokens = 60, refillDuration = 1)
    @GetMapping("/hello")
    public String hello() {
        return "Hello";
    }

    // This endpoint also uses the Token Bucket algorithm:
    // Allows 6 requests initially, then refills 5 tokens every 10 seconds.
    // The rate limit is applied globally or per default client identifier.
    @RateLimit(algorithm = AlgorithmType.TOKEN_BUCKET, capacity = 6, refillTokens = 5, refillDuration = 10)
    @GetMapping("/middleware-check")
    public String testMiddleware() {
        return "Middleware-level rate limit passed!";
    }

    // Example with Sliding Window algorithm and a custom key
    // Allows 10 requests per minute for a specific API key or user ID.
    // Replace "my-custom-api-key" with a dynamic value if needed.
    @RateLimit(algorithm = AlgorithmType.SLIDING_WINDOW, capacity = 10, refillDuration = 60, key = "my-custom-api-key")
    @GetMapping("/api/protected-by-key")
    public String protectedByKey() {
        return "This API is protected by a custom key rate limit!";
    }

    // Example with Sliding Window algorithm, per-user (default client) limit
    // Allows 20 requests per 5 minutes for each individual client.
    @RateLimit(algorithm = AlgorithmType.SLIDING_WINDOW, capacity = 20, refillDuration = 300) // 300 seconds = 5 minutes
    @GetMapping("/api/per-user-limit")
    public String perUserLimit() {
        return "This API has a per-user rate limit!";
    }
}
