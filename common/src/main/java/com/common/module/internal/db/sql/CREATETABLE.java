
package com.common.module.internal.db.sql;

import com.common.module.util.AnnotationUtil;
import com.common.module.internal.db.annotation.*;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;
import com.common.module.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class CREATETABLE {

	private static final Logger log = LoggerFactory.getLogger(CREATETABLE.class);

	public static <T extends DBEntity> String BUILDSQL(Class<T> entityType, String table) {

		String sql = "";
		List<Field> allField = new ArrayList<>(DBEntityUtils.fieldSet(entityType));
		List<Field> keyField = new ArrayList<>(DBEntityUtils.keySet(entityType));
		List<Field> indexField = new ArrayList<>(DBEntityUtils.indexSet(entityType));
		Map<String, List<Field>> indexMap = DBEntityUtils.indexsNamedMap(entityType);
		//
		long startValue = 0;
		String[] primaryKeys = new String[keyField.size()];
		int k = 0;
		for (Field field : keyField) {
			primaryKeys[k++] = DBEntityUtils.columnName(field);
		}
		sql += "create table if not exists `" + table + "` (";
		int primaryKeyIndex = 0;
		for (int j = 0; j < allField.size(); j++) {
			Field field = allField.get(j);
			String name = DBEntityUtils.columnName(field);
			Persistent column = AnnotationUtil.findAnnotation(field, Persistent.class);
			DataType dataType = column.dataType();
			String type = dataType.dbType;
			boolean canBeNull = dataType.canBeNull(field);
			Object defaultValue = dataType.defualtValue();
			String desc = column.comment();
			int len = column.len() == 0 ? dataType.len : column.len();
			int scale = column.scale() == 0 ? dataType.scale : column.scale();
			if (primaryKeyIndex < primaryKeys.length && name.equals(primaryKeys[primaryKeyIndex])) {
				primaryKeyIndex++;
				PK primaryKey = AnnotationUtil.findAnnotation(field, PK.class);
				boolean incr = (primaryKey != null && primaryKey.auto());
				if (incr) {
					startValue = primaryKey.begin();
				}
				if (incr) {
					sql += "`" + name + "` " + type + (dataType == DataType.DECIMAL ? "(" + len + "," + scale + ") " : (len == 0 ? " " : dataType == DataType.DOUBLE ? "(" + len + "," + scale + ") " : dataType == DataType.FLOAT ? "(" + len + "," + scale + ") " : "(" + len + ") ")) + (canBeNull ? "null" : "not null") + " auto_increment " + "comment  '" + desc + "',";
				} else {
					sql += "`" + name + "` " + type + (dataType == DataType.DECIMAL ? "(" + len + "," + scale + ") " : (len == 0 ? " " : dataType == DataType.DOUBLE ? "(" + len + "," + scale + ") " : dataType == DataType.FLOAT ? "(" + len + "," + scale + ") " : "(" + len + ") ")) + (canBeNull ? "null" : "not null");
					if (!canBeNull) {
						if (dataType == DataType.BOOL) {
							sql += " default b'" + defaultValue + "'";
						} else {
							sql += " default '" + defaultValue + "'";
						}
					}
					sql += " comment  '" + desc + "',";
				}
			} else {
				sql += "`" + name + "` " + type + (dataType == DataType.DECIMAL ? "(" + len + "," + scale + ") " : (len == 0 ? " " : dataType == DataType.DOUBLE ? "(" + len + "," + scale + ") " : dataType == DataType.FLOAT ? "(" + len + "," + scale + ") " : "(" + len + ") ")) + (canBeNull ? "null" : "not null");
				if (!canBeNull) {
					if (dataType == DataType.BOOL) {
						sql += " default b'" + defaultValue + "'";
					} else {
						sql += " default '" + defaultValue + "'";
					}
				}
				sql += " comment  '" + desc + "',";
			}
		}
		sql += "primary key (";
		for (int i = 0; i < primaryKeys.length; i++) {
			String id = primaryKeys[i];
			sql += "`" + id + "`";
			if (i < primaryKeys.length - 1) {
				sql += ",";
			} else {
				if (indexField.isEmpty()) {
					sql += ") ";
				} else {
					sql += "), ";
				}
			}
		}
		if (!indexMap.isEmpty()) {
			for (List<Field> list : indexMap.values()) {
				if (list.size() > 1) {
					String type = ((Index) AnnotationUtil.findAnnotation(list.get(0), Index.class)).type();
					String way = ((Index) AnnotationUtil.findAnnotation(list.get(0), Index.class)).way();
					for (int i = 1; i < list.size(); i++) {
						if (!((Index) AnnotationUtil.findAnnotation(list.get(i), Index.class)).type().equals(type) || !((Index) AnnotationUtil.findAnnotation(list.get(i), Index.class)).way().equals(way)) {
							log.error("联合索引的类型不一致clz=" + entityType + ":" + StringUtils.toString(list));
							return null;
						}
					}
				}
			}
			// INDEX `idx_c_d` (`c`, `d`) USING HASH
			int idx = 0;
			for (Entry<String, List<Field>> entry : indexMap.entrySet()) {
				String indexName = entry.getKey();
				List<Field> list = entry.getValue();
				Field first = list.get(0);
				String type = ((Index) AnnotationUtil.findAnnotation(first, Index.class)).type();
				String way = ((Index) AnnotationUtil.findAnnotation(first, Index.class)).way();
				if (type.equals(Index.INDEX_TYPE_UNIQUE) || type.equals(Index.INDEX_TYPE_FULLTEXT)) {
					sql += " " + type + " ";
				}
				if (list.size() == 1) {
					sql += "index `" + indexName + "` (`" + DBEntityUtils.columnName(first) + "`) USING " + way;
				} else {
					sql += "index `" + indexName + "` (";
					for (int i = 0; i < list.size(); i++) {
						Field field = list.get(i);
						sql += "`" + DBEntityUtils.columnName(field) + "`";
						if (i < list.size() - 1) {
							sql += ",";
						} else {
							sql += ") using " + way;
						}
					}
				}
				if (idx < indexMap.size() - 1) {
					sql += ",";
				}
				idx++;
			}
		}
		String engine = "";
		String charset = "";
		String collate = "";
		DB db = AnnotationUtil.findAnnotation(entityType, DB.class);
		engine = System.getProperty("mysql.engine", db.engine()).toLowerCase();
		charset = db.charset();
		collate = db.collate();
		sql += ") ";
		// 只有一个主键自增才有效，联合主键自增默认从1开始
		if (startValue > 1 && primaryKeys.length == 1) {
			sql += "auto_increment=" + startValue + ",";
		}
		sql += " engine=" + engine + " default charset=" + charset + " collate " + collate + " ";
		sql += " comment '" + ((Table) AnnotationUtil.findAnnotation(entityType, Table.class)).comment() + "[" + entityType.getName() + "]" + "'";
		return sql;
	}
}
