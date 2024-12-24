package com.common.module.network.grpc;

import com.common.module.cluster.entity.ServerEntity;
import com.common.module.internal.cache.redission.topic.TopicMessage;
import com.common.module.util.StringUtils;

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