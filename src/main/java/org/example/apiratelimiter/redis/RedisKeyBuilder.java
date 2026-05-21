package org.example.apiratelimiter.redis;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.apiratelimiter.extractor.JwtIdentityExtractor;
import org.example.apiratelimiter.extractor.UriExtractor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisKeyBuilder {

    private final UriExtractor uriExtractor;
    private final JwtIdentityExtractor jwtIdentityExtractor;
    public String generateKey(
            @NonNull HttpServletRequest request,
            String customKey
    ) {

        String userId =
                jwtIdentityExtractor.extractIdentity(request);

        String uri =
                uriExtractor.extract(request);

        if (customKey.isEmpty()) {

            return "rateLimit:" +
                    uri +
                    ":" +
                    userId;
        }

        return "rateLimit:" +
                customKey +
                ":" +
                userId;
    }
}