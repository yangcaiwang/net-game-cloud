package com.ycw.core.cluster.loadalance;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <集群中基础服务类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
class BaseServer {
    private String name;
    private int weight;
    private int currentConnections;
    private final Lock lock = new ReentrantLock();

    public BaseServer valueOf(String name, int weight) {
        BaseServer server = new BaseServer();
        server.name = name;
        server.weight = weight;
        return server;
    }

    public static BaseServer valueOf(String name, int weight, int conn) {
        BaseServer server = new BaseServer();
        server.name = name;
        server.weight = weight;
        server.currentConnections = conn;
        return server;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public int getCurrentConnections() {
        lock.lock();
        try {
            return currentConnections;
        } finally {
            lock.unlock();
        }
    }

    public void incrementConnections() {
        lock.lock();
        try {
            currentConnections++;
        } finally {
            lock.unlock();
        }
    }

    public void decrementConnections() {
        lock.lock();
        try {
            currentConnections--;
        } finally {
            lock.unlock();
        }
    }

    public double getConnectionWeightRatio() {
        lock.lock();
        try {
            return (double) currentConnections / weight;
        } finally {
            lock.unlock();
        }
    }
}
