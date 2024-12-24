
package com.common.module.internal.db.constant;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.sql.INSERTREPLACE;
import com.common.module.internal.db.sql.SELECT;
import com.common.module.internal.db.sql.UPDATE;

/**
 * 常用的sql语句
 */
public enum SQLType {

	/** 创建表 */
	CREATETABLE {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return com.common.module.internal.db.sql.CREATETABLE.BUILDSQL(cls, table);
		}
	}, //

	/** 插入表 */
	INSERT {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return com.common.module.internal.db.sql.INSERT.BUILDSQL(cls, table);
		}
	}, //

	/** 插入表如果不存在 */
	INSERTIGNORE {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return com.common.module.internal.db.sql.INSERTIGNORE.BUILDSQL(cls, table);
		}
	}, //

	/** 插入表 如果存在就替换 */
	REPLACE {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return INSERTREPLACE.BUILDSQL(cls, table);
		}
	}, //

	/** 插入表如果存在就更新 */
	INSERTONDUPLICATEKEYUPDATE {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return com.common.module.internal.db.sql.INSERTONDUPLICATEKEYUPDATE.BUILDSQL(cls, table);
		}
	}, //

	/** 删除指定主键的数据 */
	DELETEBYKEY {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return com.common.module.internal.db.sql.DELETEBYKEY.BUILDSQL(cls, table);
		}
	}, //

	/** 删除指定主键的数据 */
	DELETEIN {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return com.common.module.internal.db.sql.DELETEBYKEY.BUILDSQL(cls, table);
		}
	}, //

	/** 更新指定主键的数据 */
	UPDATEBYKEY {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return UPDATE.BUILDSQL(cls, table);
		}
	}, //

	/** 查找指定主键的数据 */
	SELECTBYKEY {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return SELECT.BUILDSQLBYKEY(cls, table);
		}
	}, //

	/** 查询指定条件的数据 */
	SELECTBYCONDITION {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return SELECT.BUILDSQLBYCONDITION(cls, table, "%s");
		}
	}, //

	/** 按指定主键查询or */
	SELECTOR {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return SELECTBYCONDITION.BUILDSQL(cls, table);
		}
	},

	/** 按指定主键查询in */
	SELECTIN {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return SELECTBYCONDITION.BUILDSQL(cls, table);
		}
	},

	/** 查询全表数据 */
	SELECTALL {

		@Override
		public <T extends DBEntity> String BUILDSQL(Class<T> cls, String table) {

			return SELECT.BUILDSQL(cls, table);
		}
	},//
	;

	public abstract <T extends DBEntity> String BUILDSQL(Class<T> cls, String table);
}
