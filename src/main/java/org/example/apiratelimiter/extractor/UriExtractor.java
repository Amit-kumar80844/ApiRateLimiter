package org.example.apiratelimiter.extractor;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class UriExtractor implements RequestKeyExtractor {

    public String extract(HttpServletRequest request) {
        Objects.requireNonNull(request, "HttpServletRequest cannot be null");
        return request.getRequestURI();
    }
}
