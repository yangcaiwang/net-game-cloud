
package com.common.module.internal.db.sql;

import com.common.module.internal.db.entity.DBEntity;

import java.util.ArrayList;
import java.util.List;

public class DROPFIELD {

	public static <T extends DBEntity> List<String> BUILDDROPFIELDSQL(String table, String... columns) {

		if (columns == null || columns.length < 1)
			throw new RuntimeException("没有要删除的字段 ");
		List<String> sqls = new ArrayList<String>();
		String sql = "alter table `" + table + "` ";
		for (int i = 0; i < columns.length; i++) {
			sql += "drop column `" + columns[i] + "`";
			if (i < columns.length - 1) {
				sql += ",";
			}
		}
		sqls.add(sql);
		return sqls;
	}
}
