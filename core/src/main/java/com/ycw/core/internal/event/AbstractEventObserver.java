
package com.ycw.core.internal.event;


import com.ycw.core.internal.loader.service.AbstractService;

/**
 * <事件观察者抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractEventObserver extends AbstractService implements IEventObserver {

	public AbstractEventObserver() {
		EventBusesImpl.getInstance().register(this);
	}
}
