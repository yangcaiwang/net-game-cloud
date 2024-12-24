package com.common.module.internal.db.entity;

import com.common.module.internal.event.AbstractEventObserver;

class MySQLableOptionsObserver extends AbstractEventObserver {

//    @EventSubscriber
//    private <E extends DBEntity> void recv(MySQLableOptionsChangedEvent<E> event) {
//		E entity = event.entity;
//		Class<E> entityType = entity.getEntityType();
//		IRepository<E> repository = getRepository(entityType);
//		if (repository instanceof DelayCachedRepository) {
//			DelayCachedRepository<E> delayCachedRepository = (DelayCachedRepository<E>) repository;
//			delayCachedRepository.commitSave(entity);
//		}
//    }

}
