package com.common.module.internal.cache.redission.topic;

import java.io.Serializable;

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
