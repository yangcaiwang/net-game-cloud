package com.ycw.core.network.grpc;

import com.ycw.core.cluster.ClusterService;
import com.ycw.core.cluster.ClusterServiceImpl;
import com.ycw.core.internal.cache.redission.event.TopicEvent;
import com.ycw.core.internal.cache.redission.event.TopicMessage;
import com.ycw.core.internal.event.AbstractEventObserver;
import com.ycw.core.internal.event.annotation.EventSubscriber;
import com.ycw.core.internal.loader.service.ServiceContext;

/**
 * <Grpc话题观察者实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class GrpcTopicObserver extends AbstractEventObserver {
    @EventSubscriber
    private void rec(TopicEvent<? extends TopicMessage> event) {
        TopicMessage message = event.message;
        if (message instanceof GrpcTopicMessage) {
            GrpcTopicMessage grpcTopicMsg = (GrpcTopicMessage) message;
            ClusterService clusterService = ServiceContext.getInstance().get(ClusterServiceImpl.class);
            clusterService.startGateGrpcClient((grpcTopicMsg).getGateServerEntity());
        }
    }
}
