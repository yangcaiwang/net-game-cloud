
package com.ycw.core.internal.db.sql;

import com.ycw.core.internal.db.entity.DBEntity;
import com.ycw.core.internal.db.entity.DBEntityUtils;
import com.ycw.core.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

public final class DELETEBYKEY {

	public static <T extends DBEntity> String BUILDSQL(Class<T> entityType, String table) {

		return BUILDSQL(table, DBEntityUtils.keySet(entityType));
	}

	public static <T extends DBEntity> String BUILDSQLIN(Class<T> entityType, String table, String... keys) {

		return BUILDSQL(table, DBEntityUtils.keySet(entityType), keys);
	}

	private static <T extends DBEntity> String BUILDSQL(String table, LinkedHashSet<Field> keyset) {

		String sql = "";
		sql += "delete from `" + table + "` where ";
		int i = 0;
		for (Field f : keyset) {
			String field = DBEntityUtils.columnName(f);
			if (i < keyset.size() - 1) {
				sql += field + "=? and ";
			} else {
				sql += field + "=?";
			}
			i++;
		}
		return sql;
	}

	private static <T extends DBEntity> String BUILDSQL(String table, LinkedHashSet<Field> keyset, String... keys) {

		StringBuilder sql = new StringBuilder();
		sql.append("delete from `").append(table).append("` where ");
		Field field = CollectionUtils.peekFirst(keyset);
		String column = DBEntityUtils.columnName(field);
		sql.append("`").append(column).append("` ").append(" in (");
		int i = 0;
		for (String k : keys) {
//			sql.append(k);
			if (i < keys.length - 1)
				sql.append("?,");
			else
				sql.append("?)");
			i++;
		}
		return sql.toString();
	}
}
