package org.example.apiratelimiter.aspect;

import jakarta.servlet.http.HttpServletRequest;

public interface RequestKeyExtractor {
    String extract(HttpServletRequest request);
}