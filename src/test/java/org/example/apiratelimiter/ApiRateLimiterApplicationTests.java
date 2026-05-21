package org.example.apiratelimiter;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ApiRateLimiterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRateLimitSynchronously() throws Exception {

        // First 5 requests should pass
        for (int i = 0; i < 6; i++) {

            mockMvc.perform(get("/middleware-check"))
                    .andExpect(status().isOk());
        }

        // 6th request should fail
        mockMvc.perform(get("/middleware-check"))
                .andExpect(status().isTooManyRequests());
    }
}