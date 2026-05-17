package org.example.apiratelimiter.extractor;

public interface Extractor<T> {
    String extractIdentity(T request);
}
