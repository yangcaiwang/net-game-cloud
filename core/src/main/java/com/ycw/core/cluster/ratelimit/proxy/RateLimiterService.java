package com.ycw.core.cluster.ratelimit.proxy;

import com.google.common.util.concurrent.RateLimiter;

/**
 * <集群限流接口>
 * <p>
 * ps: 谷歌限流策略
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class RateLimiterService {
    private final RateLimiter rateLimiter;

    public RateLimiterService(double permitsPerSecond) {

        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    public boolean tryAcquire() {
        return rateLimiter.tryAcquire();
    }
}