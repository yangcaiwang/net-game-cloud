package com.common.module.internal.cache.redission.topic;


import com.common.module.internal.event.AbstractEvent;

/**
 * 话题(topic)事件
 *
 @author yangcaiwang
 *
 */
public class TopicEvent<T> extends AbstractEvent {

	public final T message;

	public TopicEvent(T message) {
		super();
		this.message = message;
	}
}
