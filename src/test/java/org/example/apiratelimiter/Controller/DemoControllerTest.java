package org.example.apiratelimiter.Controller;

import org.example.apiratelimiter.configuration.RateLimitConfig;
import org.example.apiratelimiter.response.AlgorithmExecuteEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private AlgorithmExecuteEngine algorithmExecuteEngine;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test to ensure clean state
        reset(algorithmExecuteEngine);
        // No global 'when' here. Each test will set up its own sequential mock behavior.
    }

    @Test
    void helloEndpoint_allowsRequest_whenRateLimitPermits() {
        // Configure mock: First call (from GlobalFilter) returns true, Second call (from Aspect) returns true
        when(algorithmExecuteEngine.allowRequest(anyString(), any(RateLimitConfig.class)))
                .thenReturn(true) // First call (GlobalFilter)
                .thenReturn(true); // Second call (Aspect)

        ResponseEntity<String> response = restTemplate.getForEntity("/hello", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Hello");
        // Verify that allowRequest was called twice (once by filter, once by aspect)
        verify(algorithmExecuteEngine, times(2)).allowRequest(anyString(), any(RateLimitConfig.class));
    }

    @Test
    void helloEndpoint_deniesRequest_whenRateLimitExceeded() {
        // Configure mock: First call (from GlobalFilter) returns true, Second call (from Aspect) returns false
        when(algorithmExecuteEngine.allowRequest(anyString(), any(RateLimitConfig.class)))
                .thenReturn(true)  // First call (GlobalFilter) allows
                .thenReturn(false); // Second call (Aspect) denies

        ResponseEntity<String> response = restTemplate.getForEntity("/hello", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getHeaders().getFirst("X-RateLimit-Remaining")).isNull();
        assertThat(response.getHeaders().getFirst("X-RateLimit-Retry-After-Milliseconds")).isNull();
        // Verify that allowRequest was called twice (once by filter, once by aspect)
        verify(algorithmExecuteEngine, times(2)).allowRequest(anyString(), any(RateLimitConfig.class));
    }

    @Test
    void middlewareCheckEndpoint_allowsRequest_whenRateLimitPermits() {
        // Configure mock: First call (from GlobalFilter) returns true, Second call (from Aspect) returns true
        when(algorithmExecuteEngine.allowRequest(anyString(), any(RateLimitConfig.class)))
                .thenReturn(true) // First call (GlobalFilter)
                .thenReturn(true); // Second call (Aspect)

        ResponseEntity<String> response = restTemplate.getForEntity("/middleware-check", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Middleware-level rate limit passed!");
        // Verify that allowRequest was called twice (once by filter, once by aspect)
        verify(algorithmExecuteEngine, times(2)).allowRequest(anyString(), any(RateLimitConfig.class));
    }

    @Test
    void middlewareCheckEndpoint_deniesRequest_whenRateLimitExceeded() {
        // Configure mock: First call (from GlobalFilter) returns true, Second call (from Aspect) returns false
        when(algorithmExecuteEngine.allowRequest(anyString(), any(RateLimitConfig.class)))
                .thenReturn(true)  // First call (GlobalFilter) allows
                .thenReturn(false); // Second call (Aspect) denies

        ResponseEntity<String> response = restTemplate.getForEntity("/middleware-check", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getHeaders().getFirst("X-RateLimit-Remaining")).isNull();
        assertThat(response.getHeaders().getFirst("X-RateLimit-Retry-After-Milliseconds")).isNull();
        // Verify that allowRequest was called twice (once by filter, once by aspect)
        verify(algorithmExecuteEngine, times(2)).allowRequest(anyString(), any(RateLimitConfig.class));
    }
}
