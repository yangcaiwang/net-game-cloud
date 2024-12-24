package com.common.module.cluster.ratelimit.proxy;

import com.google.common.util.concurrent.RateLimiter;

public class RateLimiterService {
    private final RateLimiter rateLimiter;

    public RateLimiterService(double permitsPerSecond) {

        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}