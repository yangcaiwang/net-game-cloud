
package com.ycw.core.internal.db.entity;

/**
 * <日志仓库抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
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
