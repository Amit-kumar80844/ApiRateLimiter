package org.example.apiratelimiter.constants;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.example.apiratelimiter.extractor.JwtIdentityExtractor;
import org.example.apiratelimiter.extractor.UriExtractor;

public class RedisKeys  {
    public String generateKey(@NonNull HttpServletRequest request, String customKey) {
        UriExtractor uriExtractor = new UriExtractor();
        JwtIdentityExtractor jwtIdentityExtractor = new JwtIdentityExtractor();
        String userId = jwtIdentityExtractor.extractIdentity(request);
        String uri = uriExtractor.extract(request);
        if(customKey.isEmpty()){
            return STR."rateLimit:\{uri}:\{userId}";
        }
        return STR."rateLimit:\{customKey}:\{userId}";
    }
}
