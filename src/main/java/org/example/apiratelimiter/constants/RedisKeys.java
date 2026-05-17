package org.example.apiratelimiter.constants;

public class RedisKeys  {
    public String generateKey(String uri,String userId) {
        return "rateLimit:" + uri + ":" + userId;
    }
}
