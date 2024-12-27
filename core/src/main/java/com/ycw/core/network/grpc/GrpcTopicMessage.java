package com.ycw.core.network.grpc;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.internal.cache.redission.event.TopicMessage;
import com.ycw.core.util.StringUtils;

/**
 * <Grpc话题消息实现类>
 * <p>
 * ps: Grpc订阅redission分布式话题
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class GrpcTopicMessage extends TopicMessage {

    public ServerEntity gateServerEntity;

    public GrpcTopicMessage(String topic, ServerEntity gateServerEntity) {
        this.topic = topic;
        this.gateServerEntity = gateServerEntity;
    }

    @Override
    public String toString() {
        return StringUtils.toString(this);
    }

    public ServerEntity getGateServerEntity() {
        return gateServerEntity;
    }

    public void setGateServerEntity(ServerEntity gateServerEntity) {
        this.gateServerEntity = gateServerEntity;
    }
}