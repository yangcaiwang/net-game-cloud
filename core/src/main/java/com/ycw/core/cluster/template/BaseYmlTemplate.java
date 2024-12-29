package com.ycw.core.cluster.template;

import java.io.Serializable;

/**
 * <解析yml模版抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class BaseYmlTemplate implements Serializable {

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
