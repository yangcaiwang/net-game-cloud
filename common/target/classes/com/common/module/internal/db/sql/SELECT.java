
package com.common.module.internal.db.sql;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;
import com.common.module.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedHashSet;

public final class SELECT {

	public static <T extends DBEntity> String BUILDSQL(Class<T> entityType, String table) {

		return BUILDSQL(table);
	}

	public static <T extends DBEntity> String BUILDSQL(String table) {

		return "select * from `" + table + "`";
	}

	public static <T extends DBEntity> String BUILDSQLBYKEY(Class<T> entityType, String table) {

		return BUILDSQLBYKEY(table, DBEntityUtils.fieldSet(entityType), DBEntityUtils.keySet(entityType));
	}

	public static <T extends DBEntity> String BUILDSQLBYKEY(String table, LinkedHashSet<Field> fields, LinkedHashSet<Field> keys) {

		// StringBuilder builder = new StringBuilder();
		// builder.append("select ");// 不使用select *from
		// // 全表检索,虽然字段多了,效率也不理想,但是理论上不使用全表检索始终还是要好点,就这样吧
		// int i = 0;
		// for (Field field : fields) {
		// builder.append("`").append(DBEntityUtils.columnName(field)).append("`");
		// if (i < fields.size() - 1) {
		// builder.append(",");
		// }
		// i++;
		// }
		StringBuilder builder = new StringBuilder();
		builder.append(BUILDSELECTSQL(fields));
		builder.append("from `").append(table).append("`  where ");
		int i = 0;
		for (Field field : keys) {
			builder.append("`").append(DBEntityUtils.columnName(field)).append("`=?");
			if (i < keys.size() - 1) {
				builder.append(" and ");
			}
			i++;
		}
		builder.append(" limit 1");// 使用主键查询仅仅读取一条数据,停止全表扫描
		return builder.toString();
	}

	public static <T extends DBEntity> String BUILDKEYSIN(Class<T> entityType, Collection<Long> keys) {

		return BUILDKEYSIN(DBEntityUtils.keySet(entityType), keys);
	}

	public static <T extends DBEntity> String BUILDKEYSIN(LinkedHashSet<Field> fieldKeySet, Collection<Long> keys) {

		StringBuilder builder = new StringBuilder();
		Field field = CollectionUtils.peekFirst(fieldKeySet);
		String column = DBEntityUtils.columnName(field);
		builder.append("`").append(column).append("` ").append(" in (");
		int i = 0;
		for (Long k : keys) {
			builder.append(k);
			if (i < keys.size() - 1)
				builder.append(",");
			else
				builder.append(")");
			i++;
		}
		return builder.toString();
	}

	/**
	 * select *from table where id in (...)
	 * 
	 * @param entityType
	 * @param keys
	 * @return
	 */
	public static <T extends DBEntity> String BUILDSQLBYKEYSET(String table, Class<T> entityType, Collection<Long> keys) {

		return BUILDSQLBYKEYSET(table, DBEntityUtils.fieldSet(entityType), DBEntityUtils.keySet(entityType), keys);
	}

	public static <T extends DBEntity> String BUILDSQLBYKEYSET(String table, LinkedHashSet<Field> fields, LinkedHashSet<Field> fieldKeySet, Collection<Long> keys) {

		if (fieldKeySet.size() != 1)
			throw new RuntimeException("无法查询:" + table + "," + fieldKeySet + "," + keys);
		// StringBuilder builder = new StringBuilder();
		// builder.append("select ");
		// int i = 0;
		// for (Field field : fields) {
		// builder.append("`").append(DBEntityUtils.columnName(field)).append("`");
		// if (i < fields.size() - 1) {
		// builder.append(",");
		// }
		// i++;
		// }
		StringBuilder builder = new StringBuilder();
		builder.append(BUILDSELECTSQL(fields));
		builder.append("from `").append(table).append("`  where `");
		builder.append(BUILDKEYSIN(fieldKeySet, keys));
		// Field field = CollectionUtils.peekOnly(fieldKeySet);
		// String column = DBEntityUtils.columnName(field);
		// builder.append(column).append("` ").append(" in (");
		// String table = 0;
		// for (Long k : keys) {
		// builder.append(k);
		// if (index < keys.size() - 1)
		// builder.append(",");
		// else
		// builder.append(")");
		// index++;
		// }
		return builder.toString();
	}

	public static <T extends DBEntity> String BUILDSQLBYKEYS(Class<T> entityType, String table, Serializable[]... pksArray) {

		return BUILDSQLBYKEYS(table, DBEntityUtils.fieldSet(entityType), DBEntityUtils.keySet(entityType), pksArray);
	}

	public static <T extends DBEntity> String BUILDKEYSOR(Class<T> entityType, Serializable[]... pksArray) {

		return BUILDKEYSOR(DBEntityUtils.keySet(entityType), pksArray);
	}

	public static <T extends DBEntity> String BUILDKEYSOR(LinkedHashSet<Field> keys, Serializable[]... pksArray) {

		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < pksArray.length; j++) {
			Serializable[] pk = pksArray[j];
			builder.append("(");
			int i = 0;
			for (Field field : keys) {
				builder.append("`").append(DBEntityUtils.columnName(field)).append("`='" + pk[i] + "'");
				if (i < keys.size() - 1) {
					builder.append(" and ");
				}
				i++;
			}
			if (j < pksArray.length - 1) {
				builder.append(") or ");
			} else {
				builder.append(")");
			}
		}
		return builder.toString();
	}

	public static <T extends DBEntity> String BUILDSQLBYKEYS(String table, LinkedHashSet<Field> fields, LinkedHashSet<Field> keys, Serializable[]... pksArray) {

		// StringBuilder builder = new StringBuilder();
		// builder.append("select ");
		// int i = 0;
		// for (Field field : fields) {
		// builder.append("`").append(DBEntityUtils.columnName(field)).append("`");
		// if (i < fields.size() - 1) {
		// builder.append(",");
		// }
		// i++;
		// }
		StringBuilder builder = new StringBuilder();
		builder.append(BUILDSELECTSQL(fields));
		builder.append("from `").append(table).append("` where ");
		builder.append(BUILDKEYSOR(keys, pksArray));
		// for (int j = 0; j < pksArray.length; j++) {
		// Serializable[] pk = pksArray[j];
		// builder.append("(");
		// int i = 0;
		// for (Field field : keys) {
		// builder.append("`").append(DBEntityUtils.columnName(field)).append("`='"
		// + pk[i] + "'");
		// if (i < keys.size() - 1) {
		// builder.append(" and ");
		// }
		// i++;
		// }
		// if (j < pksArray.length - 1) {
		// builder.append(") or ");
		// } else {
		// builder.append(")");
		// }
		// }
		return builder.toString();
	}

	public static <T extends DBEntity> String BUILDSQLBYCONDITION(Class<T> entityType, String table, String condition) {

		return BUILDSQLBYCONDITION(table, DBEntityUtils.fieldSet(entityType), condition);
	}

	public static <T extends DBEntity> String BUILDSQLBYCONDITION(String table, LinkedHashSet<Field> fields, String condition) {

		// StringBuilder builder = new StringBuilder();
		// builder.append("select ");
		// int i = 0;
		// for (Field field : fields) {
		// builder.append("`").append(DBEntityUtils.columnName(field)).append("`");
		// if (i < fields.size() - 1) {
		// builder.append(",");
		// }
		// i++;
		// }
		StringBuilder builder = new StringBuilder();
		builder.append(BUILDSELECTSQL(fields));
		builder.append("from `").append(table).append("`  where ").append(condition);
		return builder.toString();
	}

	public static <T extends DBEntity> String BUILDSELECTSQL(Class<T> entityType) {

		return BUILDSELECTSQL(DBEntityUtils.fieldSet(entityType));
	}

	public static <T extends DBEntity> String BUILDSELECTSQL(LinkedHashSet<Field> fields) {

		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		int i = 0;
		for (Field field : fields) {
			builder.append("`").append(DBEntityUtils.columnName(field)).append("`");
			if (i < fields.size() - 1) {
				builder.append(",");
			}
			i++;
		}
		builder.append(" ");
		return builder.toString();
	}
}