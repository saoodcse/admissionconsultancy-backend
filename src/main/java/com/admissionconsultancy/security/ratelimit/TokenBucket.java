package com.admissionconsultancy.security.ratelimit;

import java.time.Duration;

public class TokenBucket {
    private final long capacity;
    private final long refillTokens;
    private final long refillIntervalNanos;

    private long availableTokens;
    private long lastRefillTimestamp;

    /**
     * @param capacity The maximum burst size (max tokens the bucket can hold).
     * @param refillTokens How many tokens to add every refill period.
     * @param refillPeriod The time duration of the refill period.
     */
    public TokenBucket(long capacity, long refillTokens, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillIntervalNanos = refillPeriod.toNanos();
        this.availableTokens = capacity; // Start full
        this.lastRefillTimestamp = System.nanoTime();
    }

    /**
     * Thread-safe method to consume tokens.
     */
    public synchronized boolean tryConsume(long tokensRequested) {
        refill();

        if (this.availableTokens >= tokensRequested) {
            this.availableTokens -= tokensRequested;
            return true;
        }

        return false;
    }

    /**
     * Calculates and adds tokens based on the exact time elapsed.
     * No background threads required.
     */
    private void refill() {
        long now = System.nanoTime();
        long elapsedNanos = now - lastRefillTimestamp;

        if (elapsedNanos > refillIntervalNanos) {
            long periods = elapsedNanos / refillIntervalNanos;
            long tokensToAdd = periods * refillTokens;

            this.availableTokens = Math.min(capacity, this.availableTokens + tokensToAdd);

            // If the bucket is completely full, we snap the timestamp to 'now'.
            // Otherwise, we advance the timestamp exactly by the periods calculated
            // so we don't lose fractional time (prevents token drift).
            if (this.availableTokens == capacity) {
                this.lastRefillTimestamp = now;
            } else {
                this.lastRefillTimestamp += (periods * refillIntervalNanos);
            }
        }
    }
}