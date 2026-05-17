package org.example.apiratelimiter.extractor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

public final class UriExtractor {

    private UriExtractor() {
    }

    public static String extract(HttpServletRequest request) {
        Objects.requireNonNull(request, "HttpServletRequest cannot be null");
        return request.getRequestURI();
    }
}