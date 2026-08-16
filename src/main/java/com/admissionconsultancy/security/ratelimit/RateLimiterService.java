package com.admissionconsultancy.security.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class RateLimiterService {

    private final long maxCapacity;
    private final long refillTokens;
    private final Duration refillPeriod;
    private final Cache<String, TokenBucket> buckets;
    private final Counter rateLimitBlockedCounter;

    public RateLimiterService(
            @Value("${app.ratelimit.max-capacity:20}") long maxCapacity,
            @Value("${app.ratelimit.refill-tokens:20}") long refillTokens,
            @Value("${app.ratelimit.refill-period-minutes:1}") long refillPeriodMinutes,
            @Value("${app.ratelimit.cache-max-size:100000}") long cacheMaxSize,
            @Value("${app.ratelimit.cache-expire-minutes:5}") long cacheExpireMinutes,
            MeterRegistry meterRegistry) {

        this.maxCapacity = maxCapacity;
        this.refillTokens = refillTokens;
        this.refillPeriod = Duration.ofMinutes(refillPeriodMinutes);

        this.buckets = Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterAccess(Duration.ofMinutes(cacheExpireMinutes))
                .build();

        // Initialize the metric
        this.rateLimitBlockedCounter = Counter.builder("api.ratelimit.blocked.total")
                .description("Total number of requests blocked by the rate limiter")
                .register(meterRegistry);
    }

    public boolean allowRequest(String key) {
        TokenBucket bucket = buckets.get(key, k -> new TokenBucket(maxCapacity, refillTokens, refillPeriod));
        boolean isAllowed = bucket.tryConsume(1);

        if (!isAllowed) {
            log.warn("Rate limit exceeded for key: {}", key);
            // Increment the Micrometer counter
            rateLimitBlockedCounter.increment();
        }

        return isAllowed;
    }
}