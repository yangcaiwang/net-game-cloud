package com.common.module.internal.db.entity;

@Deprecated
public class ReuseRepository extends DelayCachedRepository<ReuseEntity> {

	public ReuseEntity makeSure(String tableName) {
		return super.makeSure(tableName);
	}

	public boolean addReuseId(String tableName, Long id) {
		return makeSure(tableName).addReuseId(id);
	}

	public Long getReuseId(String tableName) {
		return makeSure(tableName).getReuseId();
	}

}
