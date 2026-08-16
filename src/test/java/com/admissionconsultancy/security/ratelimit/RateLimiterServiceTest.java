package com.admissionconsultancy.security.ratelimit;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterServiceTest {

    private MeterRegistry meterRegistry;
    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        // Initialize with small maxCapacity (2 tokens) for easy, fast unit testing
        rateLimiterService = new RateLimiterService(
                2L,     // maxCapacity
                2L,     // refillTokens
                1L,     // refillPeriodMinutes
                1000L,  // cacheMaxSize
                5L,     // cacheExpireMinutes
                meterRegistry
        );
    }

    @Test
    @DisplayName("Should allow requests when within capacity limit")
    void testAllowRequest_WithinCapacity() {
        String clientIp = "192.168.1.1";

        // First 2 requests should be allowed (maxCapacity = 2)
        boolean firstRequest = rateLimiterService.allowRequest(clientIp);
        boolean secondRequest = rateLimiterService.allowRequest(clientIp);

        assertThat(firstRequest).isTrue();
        assertThat(secondRequest).isTrue();

        // Counter should not increment for allowed requests
        assertThat(meterRegistry.get("api.ratelimit.blocked.total").counter().count()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should block requests exceeding capacity and increment blocked metric counter")
    void testAllowRequest_ExceedsCapacity_IncrementsMetric() {
        String clientIp = "192.168.1.2";

        // Consume the 2 available tokens
        rateLimiterService.allowRequest(clientIp);
        rateLimiterService.allowRequest(clientIp);

        // 3rd and 4th requests should be blocked
        boolean thirdRequest = rateLimiterService.allowRequest(clientIp);
        boolean fourthRequest = rateLimiterService.allowRequest(clientIp);

        assertThat(thirdRequest).isFalse();
        assertThat(fourthRequest).isFalse();

        // Counter should record the 2 blocked requests
        assertThat(meterRegistry.get("api.ratelimit.blocked.total").counter().count()).isEqualTo(2.0);
    }

    @Test
    @DisplayName("Should isolate rate limit buckets between different keys")
    void testAllowRequest_IsolatedKeys() {
        String clientIpA = "10.0.0.1";
        String clientIpB = "10.0.0.2";

        // Exhaust tokens for Client A
        rateLimiterService.allowRequest(clientIpA);
        rateLimiterService.allowRequest(clientIpA);
        boolean clientABlocked = rateLimiterService.allowRequest(clientIpA);

        // Client A should be blocked
        assertThat(clientABlocked).isFalse();

        // Client B should still have its own full bucket and be allowed
        boolean clientAAllowed = rateLimiterService.allowRequest(clientIpB);
        assertThat(clientAAllowed).isTrue();

        // Counter should record only 1 blocked attempt (for Client A)
        assertThat(meterRegistry.get("api.ratelimit.blocked.total").counter().count()).isEqualTo(1.0);
    }
}