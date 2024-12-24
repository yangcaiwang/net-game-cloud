
package com.common.module.internal.event;


import com.common.module.internal.loader.service.AbstractService;

public abstract class AbstractEventObserver extends AbstractService implements IEventObserver {

	public AbstractEventObserver() {
		EventBusesImpl.getInstance().register(this);
	}
}
