package com.common.module.internal.loader.service;

import com.common.module.internal.event.AbstractEvent;

public class ServiceModifiedEvent extends AbstractEvent {

	public final AbstractService service;

	public ServiceModifiedEvent(AbstractService service) {
		super();
		this.service = service;
	}
}
