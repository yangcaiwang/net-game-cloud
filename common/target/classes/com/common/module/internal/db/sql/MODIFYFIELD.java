
package com.common.module.internal.db.sql;

import com.common.module.util.AnnotationUtil;
import com.common.module.internal.db.annotation.Persistent;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MODIFYFIELD {

	public static <T extends DBEntity> List<String> BUILDSQL(String table, Field... fields) {

		if (fields == null | fields.length < 1)
			throw new RuntimeException("没有要修改的字段 ");
		List<String> sqls = new ArrayList<String>();
		String sql = "ALTER TABLE `" + table + "` ";
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String name = DBEntityUtils.columnName(field);
			Persistent column = AnnotationUtil.findAnnotation(field, Persistent.class);
			DataType dataType = column.dataType();
			String type = dataType.dbType;
			boolean canBeNull = dataType.canBeNull(field);
			Object defaultValue = dataType.defualtValue();
			String desc = column.comment();
			int len = column.len() == 0 ? dataType.len : column.len();
			int scale = column.scale() == 0 ? dataType.scale : column.scale();
			sql += "MODIFY COLUMN `" + name + "` ";
			sql += type + (dataType == DataType.DECIMAL ? "(" + len + "," + scale + ") " : (len == 0 ? " " : dataType == DataType.DOUBLE ? "(" + len + "," + scale + ") " : dataType == DataType.FLOAT ? "(" + len + "," + scale + ") " : "(" + len + ") ")) + (canBeNull ? "null" : "not null");
			if (!canBeNull) {
				if (dataType == DataType.BOOL) {
					sql += " default b'" + defaultValue + "'";
				} else {
					sql += " default '" + defaultValue + "'";
				}
			}
			sql += " comment  '" + desc + "'";
			if (i < fields.length - 1) {
				sql += ",";
			}
		}
		sqls.add(sql);
		return sqls;
	}
}
