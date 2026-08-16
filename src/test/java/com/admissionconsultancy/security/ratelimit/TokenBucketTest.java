package com.admissionconsultancy.security.ratelimit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class TokenBucketTest {

    @Test
    @DisplayName("Should allow token consumption up to bucket capacity")
    void testTryConsume_WithinCapacity() {
        // Capacity of 3 tokens, refilling 3 tokens every 1 minute
        TokenBucket bucket = new TokenBucket(3, 3, Duration.ofMinutes(1));

        assertThat(bucket.tryConsume(1)).isTrue();
        assertThat(bucket.tryConsume(1)).isTrue();
        assertThat(bucket.tryConsume(1)).isTrue();
    }

    @Test
    @DisplayName("Should reject token consumption when bucket is empty")
    void testTryConsume_ExceedsCapacity_ReturnsFalse() {
        // Capacity of 2 tokens
        TokenBucket bucket = new TokenBucket(2, 2, Duration.ofMinutes(1));

        assertThat(bucket.tryConsume(1)).isTrue();
        assertThat(bucket.tryConsume(1)).isTrue();
        // 3rd token request should fail because bucket is empty
        assertThat(bucket.tryConsume(1)).isFalse();
    }

    @Test
    @DisplayName("Should not allow consuming more tokens in a single request than the total capacity")
    void testTryConsume_SingleRequestExceedsCapacity() {
        TokenBucket bucket = new TokenBucket(5, 5, Duration.ofMinutes(1));

        // Requesting 6 tokens when capacity is 5 should fail immediately
        assertThat(bucket.tryConsume(6)).isFalse();
    }

    @Test
    @DisplayName("Should refill tokens over elapsed time intervals")
    void testRefill_OverTime() throws InterruptedException {
        // Capacity of 1 token, refilling 1 token every 10 milliseconds
        TokenBucket bucket = new TokenBucket(1, 1, Duration.ofMillis(10));

        // Consume the only available token
        assertThat(bucket.tryConsume(1)).isTrue();
        // Bucket is now empty, next immediate try should fail
        assertThat(bucket.tryConsume(1)).isFalse();

        // Wait slightly longer than the refill interval (10ms)
        Thread.sleep(25);

        // Token should be refilled, allowing consumption again
        assertThat(bucket.tryConsume(1)).isTrue();
    }

    @Test
    @DisplayName("Should cap refilled tokens at maximum capacity")
    void testRefill_CapsAtMaxCapacity() throws InterruptedException {
        // Capacity of 2 tokens, refilling 5 tokens every 10ms
        TokenBucket bucket = new TokenBucket(2, 5, Duration.ofMillis(10));

        // Sleep to allow multiple refill cycles to pass
        Thread.sleep(25);

        // Even though massive tokens were added, it should be capped at capacity (2 tokens)
        assertThat(bucket.tryConsume(1)).isTrue();
        assertThat(bucket.tryConsume(1)).isTrue();
        // 3rd token must fail because capacity maxes out at 2
        assertThat(bucket.tryConsume(1)).isFalse();
    }
}