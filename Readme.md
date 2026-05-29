# API Rate Limiter SDK

## Overview

The API Rate Limiter SDK is a **high-performance, robust, and flexible solution** engineered to safeguard your Spring Boot applications. It ensures fair resource usage, prevents abuse, and maintains high availability by precisely controlling the rate at which clients can access your APIs. Designed as a seamless Spring Boot Auto-Configuration, this SDK integrates effortlessly into existing projects, providing a critical layer of defense against malicious attacks, accidental overuse, and resource exhaustion.

## What is an API Rate Limiter?

An API Rate Limiter is a fundamental mechanism that restricts the number of requests a user or client can make to an API within a specified timeframe. Without effective rate limiting, a single client could overwhelm your servers with requests, leading to severe consequences such as denial-of-service (DoS) attacks, degraded performance for legitimate users, and exorbitant infrastructure costs. This SDK provides the tools to prevent such scenarios.

## What Can This SDK Do?

This SDK delivers a powerful, highly configurable, and easy-to-integrate rate limiting solution for your Spring Boot applications, built with scalability and reliability in mind.

### Key Features:

*   **Prevent Abuse & DoS Attacks**: Proactively protect your backend services from being overwhelmed by malicious actors or uncontrolled traffic spikes.
*   **Ensure Fair Usage**: Equitably distribute API access among all consumers, preventing any single user from monopolizing critical resources.
*   **Resource Protection**: Shield your database, external services, and computational resources from excessive and potentially damaging load.
*   **Seamless Spring Boot Integration**: Leverages Spring Boot's auto-configuration capabilities for minimal setup, allowing for rapid deployment.
*   **Redis-Backed Persistence**: Utilizes Redis for efficient, distributed, and highly scalable rate limit tracking, crucial for microservices architectures and clustered deployments.
*   **Configurable Algorithms**: Supports industry-standard rate limiting algorithms, including **Token Bucket** (for smooth rate control with burst capacity) and **Sliding Window Counter** (for accurate request counting over a rolling time window).
*   **Customizable Key Generation**: Define flexible strategies for identifying clients (e.g., by IP address, API key, user ID, or custom headers) to apply granular rate limits.
*   **Extensible Design**: Architected for easy extension and customization, allowing developers to tailor the solution to unique application requirements.

## How to Use It (Direct Integration)

To integrate this API Rate Limiter into your Spring Boot project, you can directly include the source code or build it as a local library.

### 1. Clone the Repository

First, obtain the project source code:
```bash
git clone https://github.com/Amit-kumar80844/ApiRateLimiter.git
cd ApiRateLimiter
```

### 2. Integrate into Your Project

**Option A: Include Source Code (Recommended for full control and easier debugging)**
Copy the `src/main/java/org/example/apiratelimiter` package (and its contents) directly into your Spring Boot project's `src/main/java` directory. Ensure your project's build system (Maven/Gradle) compiles these new files.

**Option B: Build and Include as a Local JAR**
1.  **Build the project:**
    ```bash
    ./gradlew clean build
    ```
2.  **Add the generated JAR to your project:** Locate the generated JAR file (e.g., `build/libs/api-rate-limiter-sdk-1.0.0.jar`) and add it as a local dependency in your project's `build.gradle` or `pom.xml`.
    *   **Gradle Example:**
        ```gradle
        dependencies {
            implementation files('path/to/your/ApiRateLimiter/build/libs/api-rate-limiter-sdk-1.0.0.jar')
        }
        ```
    *   **Maven Example:**
        ```xml
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>api-rate-limiter-sdk</artifactId>
            <version>1.0.0</version>
            <scope>system</scope>
            <systemPath>${project.basedir}/path/to/your/ApiRateLimiter/build/libs/api-rate-limiter-sdk-1.0.0.jar</systemPath>
        </dependency>
        ```
        *(Note: While `system` scope is shown for Maven, it's generally preferred to install the JAR to your local Maven repository (`mvn install:install-file ...`) for better dependency management.)*

### 3. Configure Redis

This SDK relies on Redis for storing rate limiting counters. Ensure you have a running Redis instance and configure its connection details in your `application.properties` or `application.yml`:

```properties
# application.properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
# spring.data.redis.password=yourpassword (if applicable)
# spring.data.redis.database=0 (if applicable)
```

### 4. Automatic Configuration

Thanks to Spring Boot's `@AutoConfiguration`, the `GlobalRateLimitFilter` will be automatically registered as a Spring Bean and applied to your application's request pipeline. You typically don't need to write any additional Java/Kotlin code to enable the basic rate limiting functionality.

### 5. Customization and Advanced Usage

The SDK provides a powerful `@RateLimit` annotation that allows you to define specific rate limiting rules directly on your controller methods.

*   **Endpoint-Specific Rate Limits**: Apply different rate limits to different API endpoints.
*   **Algorithm Selection**: Choose between `TOKEN_BUCKET` or `SLIDING_WINDOW_COUNTER`.
*   **Key-Based Limiting**: If you provide a `key` parameter in the `@RateLimit` annotation, the rate limit will be applied specifically to that key. If omitted, the rate limit defaults to a "per client" basis (e.g., identified by IP address).

**`@RateLimit` Annotation Parameters:**

*   `algorithm`: (`AlgorithmType.TOKEN_BUCKET` or `AlgorithmType.SLIDING_WINDOW_COUNTER`) - Specifies the rate limiting algorithm.
*   `capacity`: (int) - **(Only for `TOKEN_BUCKET`)** The maximum number of tokens the bucket can hold.
*   `refillTokens`: (int) - **(Only for `TOKEN_BUCKET`)** The number of tokens added to the bucket at each refill interval.
*   `refillDuration`: (int) - **(Only for `TOKEN_BUCKET`)** The duration in seconds after which `refillTokens` are added to the bucket.
*   `limit`: (int) - **(Only for `SLIDING_WINDOW_COUNTER`)** The maximum number of requests allowed within the `windowSize`.
*   `windowSize`: (int) - **(Only for `SLIDING_WINDOW_COUNTER`)** The duration of the sliding window in seconds.
*   `key`: (String, optional) - A custom key to identify the client for rate limiting. If not provided, a default client identifier (e.g., IP address) will be used.

## Performance & Robustness: Proven Under Load

To validate the resilience and effectiveness of this API Rate Limiter SDK under extreme conditions, a comprehensive load test was conducted. This rigorous test simulated a high-concurrency scenario, unequivocally demonstrating the system's ability to maintain stability and correctly enforce rate limits.

### Load Test Scenario:
*   **Simulated Users:** 100,000 concurrent users.
*   **Target Endpoint:** `/hello` (configured with `TOKEN_BUCKET` algorithm, `capacity = 5`, `refillTokens = 60`, `refillDuration = 1`).
*   **Test Duration:** Approximately 23 seconds.

### Key Results:
The test results unequivocally confirm the rate limiter's robust performance and precision:
*   **Total Requests Processed:** 100,000
*   **Successful Requests (200 OK):** 5 (Precisely matching the configured `capacity` before throttling)
*   **Rate-Limited Requests (429 Too Many Requests):** 99,995
*   **Other Errors:** 0
*   **Effective Rate-Limited Percentage:** 99.995% (demonstrating near-perfect throttling of excess traffic)
*   **Total Execution Time:** 23.6 seconds

These results highlight the SDK's exceptional capability to:
*   **Effectively Throttle Traffic:** Accurately identify and block requests exceeding the defined rate limits with minimal overhead.
*   **Maintain System Stability:** Gracefully handle a massive influx of concurrent requests without degradation, errors, or service interruptions.
*   **Ensure Fair Resource Allocation:** Allow initial legitimate requests while decisively preventing abuse and resource exhaustion.

A detailed report of the load test, including execution logs and further metrics, can be found in the `LoadTest.html` file located at the root of this project. This report serves as tangible evidence of the SDK's production-readiness and its critical role in safeguarding API infrastructure.

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
    // Allows 6 requests initially, then refills 5 tokens every 1 second.
    // The rate limit is applied globally or per default client identifier.
    @RateLimit(algorithm = AlgorithmType.TOKEN_BUCKET, capacity = 6, refillTokens = 5, refillDuration = 1)
    @GetMapping("/middleware-check")
    public String testMiddleware() {
        return "Middleware-level rate limit passed!";
    }

    // Example with Sliding Window Counter algorithm and a custom key:
    // Allows 7 requests within a 1-second window for the key "specific-sw-key".
    @RateLimit(algorithm = AlgorithmType.SLIDING_WINDOW_COUNTER, limit = 7, windowSize = 1, key = "specific-sw-key")
    @GetMapping("/sliding-window-with-key")
    public String slidingWindowWithKey() {
        return "Sliding Window (with-key) endpoint!";
    }

    // Example with Sliding Window Counter algorithm, per-user (default client) limit:
    // Allows 20 requests within a 300-second (5-minute) window for each individual client.
    @RateLimit(algorithm = AlgorithmType.SLIDING_WINDOW_COUNTER, limit = 20, windowSize = 300)
    @GetMapping("/api/per-user-limit")
    public String perUserLimit() {
        return "This API has a per-user rate limit!";
    }
}

### 6. Terms of Use

This SDK is provided under the [MIT License / Apache 2.0 License / Your Chosen License]. By using this SDK, you agree to the terms specified in the LICENSE file. It is intended to be used as a tool to enhance the resilience and security of your applications. While designed for robustness, it is crucial to perform thorough testing in your specific environment. The authors and contributors are not liable for any damages or issues arising from its use.
