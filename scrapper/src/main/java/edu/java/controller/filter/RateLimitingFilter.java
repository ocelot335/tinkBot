package edu.java.controller.filter;

import edu.java.configuration.ApplicationConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class RateLimitingFilter implements Filter {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private ApplicationConfig.RateLimits rateLimits;

    public RateLimitingFilter(ApplicationConfig.RateLimits rateLimits) {
        this.rateLimits = rateLimits;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        Bucket bucket = resolveBucket(httpServletRequest);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            chain.doFilter(request, response);
        } else {
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            httpServletResponse.getWriter().append("Too many requests");
            httpServletResponse.getWriter().flush();
        }
    }

    private Bucket resolveBucket(HttpServletRequest request) {
        return cache.computeIfAbsent(request.getRemoteAddr(), this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit =
            Bandwidth.classic(rateLimits.capacity(), Refill.intervally(rateLimits.tokens(), rateLimits.period()));
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}
