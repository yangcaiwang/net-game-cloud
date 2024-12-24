
package com.common.module.internal.db.sql;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;

/**
 * REPLACE的运行与INSERT很相像,但是如果旧记录与新记录有相同的值,则在新记录被插入之前,旧记录被删除
 */
public final class INSERTREPLACE {

	public static <T extends DBEntity> String BUILDSQL(Class<T> entityType, String table) {

		return BUILDVALUESSQL(entityType, table);
	}

	/**
	 * 去掉字段,直接传值,提高效率,如果和服导数据结构不一致就蛋疼了
	 * 
	 * @param entityType
	 * @return
	 */
	public static <T extends DBEntity> String BUILDVALUESSQL(Class<T> entityType, String table) {

		StringBuilder builder = new StringBuilder();
		builder.append("replace into `").append(table).append("` (");
//		builder.append("insert into `").append(table).append("` (");
		LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(entityType);
		int i = 0;
		for (Field field : fields) {
			String name = DBEntityUtils.columnName(field);
			if (i < fields.size() - 1) {
				builder.append("`").append(name).append("`,");
			} else {
				builder.append("`").append(name).append("`)");
			}
			i++;
		}
		builder.append(" values(");
//		LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(entityType);
		for (int j = 0; j < fields.size(); j++) {
			if (j < fields.size() - 1) {
				builder.append("?,");
			} else {
				builder.append("?)");
			}
		}
		return builder.toString();
	}
}
