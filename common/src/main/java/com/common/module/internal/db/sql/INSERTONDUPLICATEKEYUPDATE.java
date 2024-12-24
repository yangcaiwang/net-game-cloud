
package com.common.module.internal.db.sql;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

public final class INSERTONDUPLICATEKEYUPDATE {

	public static <T extends DBEntity> String BUILDSQL(Class<T> entityType, String table) {

		return BUILDINSERTSQL(entityType, table) + " on duplicate key update " + BUILDUPDSQL(entityType);
	}

	private static <T extends DBEntity> String BUILDINSERTSQL(Class<T> entityType, String table) {

		StringBuilder sql = new StringBuilder();
		sql.append("insert into `").append(table).append("` (");
		LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(entityType);
		int i = 0;
		for (Field field : fields) {
			String name = DBEntityUtils.columnName(field);
			if (i < fields.size() - 1) {
				sql.append("`").append(name).append("`,");
			} else {
				sql.append("`").append(name).append("`)");
			}
			i++;
		}
		sql.append(" values(");
		for (int j = 0; j < fields.size(); j++) {
			if (j < fields.size() - 1) {
				sql.append("?,");
			} else {
				sql.append("?)");
			}
			i++;
		}
		return sql.toString();
	}

	private static <T extends DBEntity> String BUILDUPDSQL(Class<T> entityType) {

		String sql = "";
		LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(entityType);
		int i = 0;
		for (Field field : fields) {
			String name = DBEntityUtils.columnName(field);
			if (i < fields.size() - 1) {
				sql += "`" + name + "`=?, ";
			} else {
				sql += "`" + name + "`=? ";
			}
			i++;
		}
		return sql;
	}
}
