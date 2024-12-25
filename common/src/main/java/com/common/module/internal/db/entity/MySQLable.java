
package com.common.module.internal.db.entity;

import com.common.module.internal.db.dao.DaoImpl;

import java.io.Serializable;
import java.util.Collection;

/**
 * <MySQL持久化对象接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface MySQLable {

    /**
     * mysql主键[],可以是联合主键,默认是通过反射取值,子类尽量重写该方法,明确主键类型和顺序
     */
    Serializable[] getPks();

    /**
     * MySQL操作接口
     */
    interface IOpts {

        /**
         * 保存数据,持久化到数据库
         */
        <T extends DBEntity> boolean saving(Class<T> entityType, Collection<T> c);

    }

    /**
     * 持久化操作类型
     */
    enum Options implements IOpts {

        /**
         * 插入
         */
        INSERT {
            @Override
            public <T extends DBEntity> boolean saving(Class<T> entityType, Collection<T> c) {
                return DaoImpl.getInstance().insert(c, entityType);
            }
        },

        /**
         * 无操作
         */
        NONE {
            @Override
            public <T extends DBEntity> boolean saving(Class<T> entityType, Collection<T> c) {
                return DaoImpl.getInstance().insertOrUpdate(c, entityType);
            }
        },

        /**
         * 替換
         */
        REPLACE {
            @Override
            public <T extends DBEntity> boolean saving(Class<T> entityType, Collection<T> c) {
                return DaoImpl.getInstance().replace(c, entityType);
            }
        },
        /**
         * 更新
         */
        UPDATE {
            @Override
            public <T extends DBEntity> boolean saving(Class<T> entityType, Collection<T> c) {
                return DaoImpl.getInstance().update(c, entityType);
            }
        },

        /**
         * 删除
         */
        DELETE {
            @Override
            public <T extends DBEntity> boolean saving(Class<T> entityType, Collection<T> c) {
                return DaoImpl.getInstance().delete(c, entityType);
            }
        },
    }
}
