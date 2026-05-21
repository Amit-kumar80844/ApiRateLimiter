package org.example.apiratelimiter.extractor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class JwtIdentityExtractor implements IdentityExtractor<HttpServletRequest >{
    @Override
    public String extractIdentity(HttpServletRequest  request) {
        String token =
                request.getHeader("Authorization");
        return decodeUserId(token);
    }

    // This method will be changed by the client to properly decode the JWT and extract the user ID.
    private String decodeUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            return "user-" + jwt.hashCode(); // This should be the actual user ID extracted from the JWT
        }
        return null;
    }
}