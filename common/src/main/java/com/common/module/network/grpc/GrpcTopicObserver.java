package com.common.module.network.grpc;

import com.common.module.cluster.ClusterService;
import com.common.module.internal.cache.redission.topic.TopicEvent;
import com.common.module.internal.cache.redission.topic.TopicMessage;
import com.common.module.internal.event.AbstractEventObserver;
import com.common.module.internal.loader.service.ServiceContext;

public class GrpcTopicObserver extends AbstractEventObserver {
    private void grpcClientEvent(TopicEvent<? extends TopicMessage> event) {
        TopicMessage message = event.message;
        if (message instanceof GrpcTopicMessage) {
            GrpcTopicMessage grpcTopicMsg = (GrpcTopicMessage) message;
            ClusterService clusterService = ServiceContext.getInstance().get(ClusterService.class);
            clusterService.startGateGrpcClient((grpcTopicMsg).getGateServerEntity());
        }
    }
}
