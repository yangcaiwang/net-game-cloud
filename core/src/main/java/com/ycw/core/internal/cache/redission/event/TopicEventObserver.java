package com.ycw.core.internal.cache.redission.event;

import com.ycw.core.internal.event.AbstractEventObserver;
import com.ycw.core.internal.event.annotation.EventSubscriber;

/**
 * <Grpc话题观察者实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class TopicEventObserver extends AbstractEventObserver {
    @EventSubscriber
    private void rec(TopicEvent<? extends TopicMessage> event) {
        TopicMessage message = event.message;
    }
}
