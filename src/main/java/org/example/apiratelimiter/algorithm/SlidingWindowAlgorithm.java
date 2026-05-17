package org.example.apiratelimiter.algorithm;

public class SlidingWindowAlgorithm {
    private final String luaScript =
        "local key = KEYS[1]\n" +
        "local window = tonumber(ARGV[1])\n" +
        "local limit = tonumber(ARGV[2])\n" +
        "local now = tonumber(ARGV[3])\n" +
        "local min = now - window\n" +
        "redis.call('ZREMRANGEBYSCORE', key, 0, min)\n" +
        "local count = redis.call('ZCARD', key)\n" +
        "if count < limit then\n" +
        "    redis.call('ZADD', key, now, now)\n" +
        "    redis.call('EXPIRE', key, window / 1000)\n" +
        "    return 1\n" +
        "else\n" +
        "    return 0\n" +
        "end";

    public boolean isAllowed(String key, int limit, long windowSizeInMs) {
        // Implementation logic for executing the Lua script via Redis would go here
        return true;
    }

}
