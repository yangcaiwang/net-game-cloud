
package com.common.module.internal.db.constant;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.sql.INSERTREPLACE;
import com.common.module.internal.db.sql.SELECT;
import com.common.module.internal.db.sql.UPDATE;

/**
 * <Sql语句模版枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum SQLType {

    /**
     * 创建表
     */
    CREATE_TABLE {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return com.common.module.internal.db.sql.CREATETABLE.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 插入表
     */
    INSERT {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return com.common.module.internal.db.sql.INSERT.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 插入表如果不存在
     */
    INSERT_IGNORE {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return com.common.module.internal.db.sql.INSERTIGNORE.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 插入表 如果存在就替换
     */
    REPLACE {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return INSERTREPLACE.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 插入表如果存在就更新
     */
    INSERT_EXIST_KEY_UPDATE {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return com.common.module.internal.db.sql.INSERTONDUPLICATEKEYUPDATE.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 删除指定主键的数据
     */
    DELETE_BY_KEY {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return com.common.module.internal.db.sql.DELETEBYKEY.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 删除指定主键的数据
     */
    DELETE_IN {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return com.common.module.internal.db.sql.DELETEBYKEY.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 更新指定主键的数据
     */
    UPDATE_BY_KEY {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return UPDATE.BUILDSQL(cls, table);
        }
    }, //

    /**
     * 查找指定主键的数据
     */
    SELECT_BY_KEY {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return SELECT.BUILDSQLBYKEY(cls, table);
        }
    }, //

    /**
     * 查询指定条件的数据
     */
    SELECT_BY_CONDITION {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return SELECT.BUILDSQLBYCONDITION(cls, table, "%s");
        }
    }, //

    /**
     * 按指定主键查询or
     */
    SELECTOR {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return SELECT_BY_CONDITION.BUILDSQL(cls, table);
        }
    },

    /**
     * 按指定主键查询in
     */
    SELECT_IN {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return SELECT_BY_CONDITION.BUILDSQL(cls, table);
        }
    },

    /**
     * 查询全表数据
     */
    SELECT_ALL {
        @Override
        public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

            return SELECT.BUILDSQL(cls, table);
        }
    },//
    ;

    public abstract <T extends DBEntity> String BUILDSQL(Class<T> cls, String table);
}
