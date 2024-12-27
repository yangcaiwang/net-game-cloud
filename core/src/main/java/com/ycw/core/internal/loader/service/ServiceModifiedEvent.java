package com.ycw.core.internal.loader.service;

import com.ycw.core.internal.event.AbstractEvent;

/**
 * <业务接口修改事件类>
 * <p>
 * ps: 用于热更
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServiceModifiedEvent extends AbstractEvent {

	public final AbstractService service;

	public ServiceModifiedEvent(AbstractService service) {
		super();
		this.service = service;
	}
}
