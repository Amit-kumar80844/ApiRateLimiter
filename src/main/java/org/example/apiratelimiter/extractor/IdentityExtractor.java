package org.example.apiratelimiter.extractor;

public interface IdentityExtractor<T> {
    String extractIdentity(T request);
}
