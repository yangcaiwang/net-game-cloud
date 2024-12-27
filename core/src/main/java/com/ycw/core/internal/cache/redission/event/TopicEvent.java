package com.ycw.core.internal.cache.redission.event;


import com.ycw.core.internal.event.AbstractEvent;

/**
 * <话题(topic)事件类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class TopicEvent<T> extends AbstractEvent {

	public final T message;

	public TopicEvent(T message) {
		super();
		this.message = message;
	}
}
