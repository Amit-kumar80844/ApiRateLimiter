package org.example.apiratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode; // Import HttpStatusCode
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoadTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final int NUMBER_OF_USERS = 100_000;
    private static final String ENDPOINT = "/hello";

    @Test
    void testHighConcurrencyRateLimiting() throws InterruptedException {
        System.out.println("Starting load test for " + NUMBER_OF_USERS + " users on endpoint " + ENDPOINT);
        String baseUrl = "http://localhost:" + port;

        ExecutorService executorService = Executors.newFixedThreadPool(200); // Use a reasonable thread pool size
        List<Callable<HttpStatusCode>> tasks = new ArrayList<>(); // Changed to HttpStatusCode

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger rateLimitedCount = new AtomicInteger(0);
        AtomicInteger otherErrorCount = new AtomicInteger(0);

        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            tasks.add(() -> {
                try {
                    ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + ENDPOINT, String.class);
                    if (response.getStatusCode().value() == HttpStatus.OK.value()) { // Compare integer values
                        successCount.incrementAndGet();
                    }
                    return response.getStatusCode(); // Returns HttpStatusCode
                } catch (HttpClientErrorException e) {
                    if (e.getStatusCode().value() == HttpStatus.TOO_MANY_REQUESTS.value()) { // Compare integer values
                        rateLimitedCount.incrementAndGet();
                    } else {
                        otherErrorCount.incrementAndGet();
                        System.err.println("Other HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
                    }
                    return e.getStatusCode(); // Returns HttpStatusCode
                } catch (Exception e) {
                    otherErrorCount.incrementAndGet();
                    System.err.println("Unexpected error: " + e.getMessage());
                    return HttpStatus.INTERNAL_SERVER_ERROR; // Return a specific HttpStatusCode for unexpected errors
                }
            });
        }

        long startTime = System.currentTimeMillis();
        List<Future<HttpStatusCode>> futures = executorService.invokeAll(tasks); // Changed to HttpStatusCode

        // Wait for all tasks to complete and collect results
        for (Future<HttpStatusCode> future : futures) { // Changed to HttpStatusCode
            try {
                future.get(); // This will rethrow exceptions if any occurred in the Callable
            } catch (ExecutionException e) {
                System.err.println("Task execution failed: " + e.getCause().getMessage());
                otherErrorCount.incrementAndGet();
            }
        }
        long endTime = System.currentTimeMillis();

        executorService.shutdown();
        // Await termination with a timeout to ensure all threads are done
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Executor service did not terminate in time.");
        }

        System.out.println("Load test completed.");
        System.out.println("Total requests: " + NUMBER_OF_USERS);
        System.out.println("Successful requests (200 OK): " + successCount.get());
        System.out.println("Rate-limited requests (429 Too Many Requests): " + rateLimitedCount.get());
        System.out.println("Other errors: " + otherErrorCount.get());
        System.out.println("Total time taken: " + (endTime - startTime) + " ms");

        // Assertions
        assertEquals(NUMBER_OF_USERS, successCount.get() + rateLimitedCount.get() + otherErrorCount.get(),
                "Total requests count mismatch");

        // Given the rate limit on /hello (capacity = 5, refillTokens = 60/sec),
        // with 100,000 concurrent users, we expect most to be rate-limited.
        // We should see some successful requests (initial capacity) and many 429s.
        assertTrue(successCount.get() > 0, "Expected at least some successful requests.");
        assertTrue(rateLimitedCount.get() > 0, "Expected a significant number of rate-limited requests.");
        // Optionally, you can assert a minimum percentage of rate-limited requests
        // For 100,000 concurrent requests against a capacity of 5, almost all should be rate-limited.
        // Let's say at least 99% should be rate-limited.
        double rateLimitedPercentage = (double) rateLimitedCount.get() / NUMBER_OF_USERS * 100;
        System.out.printf("Rate-limited percentage: %.2f%%\n", rateLimitedPercentage);
        assertTrue(rateLimitedPercentage > 99.0, "Expected more than 99% of requests to be rate-limited.");
    }
}
