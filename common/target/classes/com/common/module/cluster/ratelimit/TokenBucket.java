package com.common.module.cluster.ratelimit;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <令牌桶算法限流策略>
 * <p>
 * ps: 能够以固定的速率向令牌桶中添加令牌，并且每个请求需要消耗一个令牌。如果桶中没有令牌，则请求会被拒绝或可以选择延迟处理
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class TokenBucket {
    private final long maxTokens; // 桶的最大容量
    private long availableTokens; // 当前可用的令牌数
    private final long refillRate; // 令牌填充速率（每秒添加的令牌数）
    private long lastRefillTimestamp; // 上次填充令牌的时间戳
    private final long delayMillisecond; // 令牌满了延时多少毫秒
    private final Lock lock = new ReentrantLock(); // 锁，用于同步

    public TokenBucket(long maxTokens, long refillRate, long delayMillisecond) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.availableTokens = maxTokens;
        this.lastRefillTimestamp = System.currentTimeMillis();
        this.delayMillisecond = delayMillisecond;
    }

    /**
     * 尝试获取一个令牌
     *
     * @return 如果获取成功返回true，否则返回false
     */
    public boolean tryAcquire() {
        lock.lock();
        try {
            refill(); // 先填充令牌
            if (availableTokens > 0) {
                availableTokens--;
                return true;
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    /**
     * 填充令牌到桶中
     */
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRefillTimestamp;

        // 计算需要添加的令牌数
        long tokensToAdd = (elapsedTime / 1000) * refillRate;
        if (tokensToAdd > 0) {
            availableTokens = Math.min(maxTokens, availableTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }

    /**
     * 等待直到获取一个令牌
     */
    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (availableTokens == 0) {
                // 等待一段时间，然后重新尝试
                lock.unlock();
                Thread.sleep(delayMillisecond);
                lock.lock();
                refill();
            }
            availableTokens--;
        } finally {
            lock.unlock();
        }
    }
}