
package com.common.module.internal.db.entity;

/**
 * 日志仓库,</br>
 * 默认是自增主键,对象直接new出来, 设置完各种属性后调用仓库的save方法</br>
 * 如果主键id设置为不自增,那么在save之前需要手动setId去自行处理主键</br>
 * 可以适当把入库时间调短,比如:(delay=10)</br>
 *
 * @param <E>
 */
abstract public class LoggerRepository<E extends LoggerEntity> extends DelayCachedRepository<E> {

	public LoggerRepository() {
		super();
	}

	@Override
	public boolean save(E obj) {
		if (obj.isAuto() && obj.getId() != 0) {
			throw new RuntimeException("自增主键不可以设置主键的值:" + entityType);
		}
		if (!obj.isAuto() && obj.getId() == 0) {
			throw new RuntimeException("非自增主键必须设置主键的值:" + entityType);
		}
		if (!super.save(obj)) {
			throw new RuntimeException();
		}
		return true;
	}

	@Override
	public long makePk() {
		throw new RuntimeException();
	}

	@Override
	public void resetPk() {
		throw new RuntimeException();
	}

}
