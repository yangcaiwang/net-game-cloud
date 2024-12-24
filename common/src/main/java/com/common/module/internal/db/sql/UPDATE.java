
package com.common.module.internal.db.sql;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

public final class UPDATE {

	public static <T extends DBEntity> String BUILDSQL(Class<T> entityType, String table) {

		return BUILDSQL(table, Lists.newArrayList(DBEntityUtils.valueSet(entityType)), Lists.newArrayList(DBEntityUtils.keySet(entityType)));
	}

	private static <T extends DBEntity> String BUILDSQL(String table, List<Field> values, List<Field> keys) {

		values.removeIf(f -> DBEntityUtils.isReadOnly(f));
		String sql = "update `" + table + "` set ";
		for (int i = 0; i < values.size(); i++) {
			Field field = values.get(i);
			String name = DBEntityUtils.columnName(field);
			if (i < values.size() - 1) {
				sql += "`" + name + "`=?, ";
			} else {
				sql += "`" + name + "`=? ";
			}
		}
		sql += " where ";
		for (int i = 0; i < keys.size(); i++) {
			Field field = keys.get(i);
			String name = DBEntityUtils.columnName(field);
			if (i < keys.size() - 1) {
				sql += "`" + name + "`=? and ";
			} else {
				sql += "`" + name + "`=?";
			}
		}
		return sql;
	}
}
