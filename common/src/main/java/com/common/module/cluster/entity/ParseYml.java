package com.common.module.cluster.entity;

import java.io.Serializable;

/**
 * <服务器解析yml类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ParseYml implements Serializable {
    /**
     * ip
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 心跳时间
     */
    private long heartbeatTime;

    /**
     * 心跳超时时间
     */
    private long heartbeatTimeout;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(long heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public long getHeartbeatTimeout() {
        return heartbeatTimeout;
    }

    public void setHeartbeatTimeout(long heartbeatTimeout) {
        this.heartbeatTimeout = heartbeatTimeout;
    }
}
