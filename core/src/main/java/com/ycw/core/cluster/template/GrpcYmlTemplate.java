package com.ycw.core.cluster.template;

/**
 * <grpc解析yml模版类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class GrpcYmlTemplate extends AbstractYmlTemplate {
    /**
     * 心跳时间
     */
    private long heartbeatTime;

    /**
     * 心跳超时时间
     */
    private long heartbeatTimeout;

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

    @Override
    public String toString() {
        return "GrpcYmlTemplate{" +
                "heartbeatTime=" + heartbeatTime +
                ", heartbeatTimeout=" + heartbeatTimeout +
                '}';
    }
}
