
package com.common.module.internal.event;

import com.common.module.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象事件
 */
public abstract class AbstractEvent implements IEvent {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public String toString() {

		return StringUtils.toString(this);
	}
}
