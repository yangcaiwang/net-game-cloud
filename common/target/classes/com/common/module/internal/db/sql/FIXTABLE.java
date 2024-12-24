
package com.common.module.internal.db.sql;

import com.common.module.util.AnnotationUtil;
import com.common.module.internal.db.annotation.Index;
import com.common.module.internal.db.annotation.PK;
import com.common.module.internal.db.annotation.Persistent;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;
import com.common.module.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class FIXTABLE {

	/**
	 * 添加字段
	 * 
	 * @param table
	 *            数据表
	 * @param addMap
	 *            当前字段:前一个字段
	 * @return
	 */
	public static <T extends DBEntity> String BUILDADDFIELDSQL(String table, LinkedHashMap<Field, Field> addMap) {

		if (CollectionUtils.isEmpty(addMap))
			throw new RuntimeException("没有要增加的字段 ");
		String sql = "ALTER TABLE `" + table + "` ";
		int i = 0;
		for (Field field : addMap.keySet()) {
			sql += " ADD COLUMN ";
			String name = DBEntityUtils.columnName(field);
			Field before = addMap.get(field);
			String beforeName = before == null ? null : DBEntityUtils.columnName(before);
			Persistent column = AnnotationUtil.findAnnotation(field, Persistent.class);
			DataType dataType = column.dataType();
			String type = dataType.dbType;
			boolean canBeNull = dataType.canBeNull(field);
			Object defaultValue = dataType.defualtValue();
			int len = column.len() == 0 ? dataType.len : column.len();
			int scale = column.scale() == 0 ? dataType.scale : column.scale();
			String desc = column.comment();
			sql += "`" + name + "` " + type + (dataType == DataType.DECIMAL ? "(" + len + "," + scale + ") " : (len == 0 ? " " : dataType == DataType.DOUBLE ? "(" + len + "," + scale + ") " : dataType == DataType.FLOAT ? "(" + len + "," + scale + ") " : "(" + len + ") ")) + (canBeNull ? "null" : "not null");
			if (!canBeNull) {
				if (dataType == DataType.BOOL) {
					sql += " default b'" + defaultValue + "'";
				} else {
					sql += " default '" + defaultValue + "'";
				}
			}
			sql += " comment  '" + desc + "'";
			if (beforeName != null) {
				sql += " after `" + beforeName + "` ";
			} else {
				sql += " first ";
			}
			if (i++ < addMap.size() - 1) {
				sql += ",";
			} else {
				sql += ";";
			}
		}
		return sql;
	}

	/**
	 * 添加字段属性
	 * 
	 * @param table
	 * @param field
	 * @return
	 */
	public static String BUILDADDFIELDATTRSQL(String table, Field field) {

		if (AnnotationUtil.isAnnotation(field, PK.class)) {
			return FIXINDEX.BUILDADDPRIMARYKEYSQL(table, DBEntityUtils.columnName(field));
		} else {
			Index index = AnnotationUtil.findAnnotation(field, Index.class);
			if (index != null) {
				if (index.type().equals(Index.INDEX_TYPE_UNIQUE)) {
					return FIXINDEX.BUILDADDUNIQUESQL(table, DBEntityUtils.columnName(field), index.way());
				} else if (index.type().equals(Index.INDEX_TYPE_FULLTEXT)) {
					return FIXINDEX.BUILDADDFULLTEXTINDEXSQL(table, DBEntityUtils.columnName(field), index.way());
				} else {
					return FIXINDEX.BUILDADDNORMALINDEXSQL(table, DBEntityUtils.indexName(field), DBEntityUtils.columnName(field), index.way());
				}
			} else {
				return null;
			}
		}
	}
}
