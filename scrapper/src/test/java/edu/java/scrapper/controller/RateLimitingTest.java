package edu.java.scrapper.controller;

import edu.java.configuration.ApplicationConfig;
import edu.java.controller.filter.RateLimitingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitingTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ApplicationConfig.RateLimits rateLimits = new ApplicationConfig.RateLimits(10L, 1L, Duration.ofMinutes(1));
        filter = new RateLimitingFilter(rateLimits);
    }

    @Test
    void doFilter_WithinRateLimit_ShouldAllowRequest() throws IOException, ServletException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(response, Mockito.never()).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    void doFilter_ExceedRateLimit_ShouldBlockRequest() throws IOException, ServletException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(response.getWriter()).thenReturn(mock(PrintWriter.class));

        for (int i = 0; i < 100; i++) {
            filter.doFilter(request, response, chain);
        }

        verify(chain, times(10)).doFilter(request, response);
        verify(response, times(90)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}
