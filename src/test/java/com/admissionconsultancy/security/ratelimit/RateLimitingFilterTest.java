package com.admissionconsultancy.security.ratelimit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private RateLimiterService rateLimiterService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private RateLimitingFilter rateLimitingFilter;

    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(responseWriter);
        lenient().when(response.getWriter()).thenReturn(printWriter);
    }

    @Test
    @DisplayName("Should allow request to proceed when rate limiter permits")
    void testDoFilterInternal_Allowed() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(rateLimiterService.allowRequest("127.0.0.1")).thenReturn(true);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    }

    @Test
    @DisplayName("Should block request and return 429 status and JSON error when rate limit exceeded")
    void testDoFilterInternal_Blocked() throws ServletException, IOException {
        when(request.getRemoteAddr()).thenReturn("192.168.1.50");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(rateLimiterService.allowRequest("192.168.1.50")).thenReturn(false);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(any(), any());
        verify(response, times(1)).setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        verify(response, times(1)).setContentType("application/json");

        assertThat(responseWriter.toString()).contains("Too many requests");
    }

    @Test
    @DisplayName("Should extract client IP correctly from valid X-Forwarded-For header")
    void testGetClientIp_WithXForwardedFor() throws ServletException, IOException {
        String remoteAddr = "10.0.0.1";
        String forwardedIp = "203.0.113.195";

        when(request.getRemoteAddr()).thenReturn(remoteAddr);
        // Include remoteAddr inside the header so !xfHeader.contains(remoteAddr) evaluates to false
        when(request.getHeader("X-Forwarded-For")).thenReturn(forwardedIp + ", " + remoteAddr);
        when(rateLimiterService.allowRequest(forwardedIp)).thenReturn(true);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimiterService, times(1)).allowRequest(forwardedIp);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should fall back to remoteAddr if X-Forwarded-For header does not contain remoteAddr")
    void testGetClientIp_XForwardedForFallback() throws ServletException, IOException {
        String remoteAddr = "127.0.0.1";

        when(request.getRemoteAddr()).thenReturn(remoteAddr);
        when(request.getHeader("X-Forwarded-For")).thenReturn("spoofed.ip.address");
        when(rateLimiterService.allowRequest(remoteAddr)).thenReturn(true);

        rateLimitingFilter.doFilterInternal(request, response, filterChain);

        verify(rateLimiterService, times(1)).allowRequest(remoteAddr);
        verify(filterChain, times(1)).doFilter(request, response);
    }
}