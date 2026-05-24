package org.example.apiratelimiter.enums;

public enum AlgorithmType {
    TOKEN_BUCKET,
    SLIDING_WINDOW_COUNTER,
    FIXED_WINDOW,
    LEAKY_BUCKET
}

/**
 * Currently, only TOKEN_BUCKET and SLIDING_WINDOW_COUNTER are implemented.
 * Other types are placeholders for future extensions.
 */
