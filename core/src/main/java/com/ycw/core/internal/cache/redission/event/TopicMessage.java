package com.ycw.core.internal.cache.redission.event;

import java.io.Serializable;

/**
 * <话题(topic)消息抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class TopicMessage implements Serializable {

    /**
     * 话题
     */
    public String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
