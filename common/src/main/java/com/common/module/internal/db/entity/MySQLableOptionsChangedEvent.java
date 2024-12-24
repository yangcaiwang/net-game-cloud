package com.common.module.internal.db.entity;

import com.common.module.internal.event.AbstractEvent;

class MySQLableOptionsChangedEvent<E extends DBEntity> extends AbstractEvent {

	final public E entity;

	MySQLableOptionsChangedEvent(E entity) {
		super();
		this.entity = entity;
	}

}
