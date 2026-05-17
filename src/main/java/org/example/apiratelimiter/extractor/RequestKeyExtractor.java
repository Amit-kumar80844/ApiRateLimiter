package org.example.apiratelimiter.extractor;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestKeyExtractor {
    String extract(HttpServletRequest request);
}