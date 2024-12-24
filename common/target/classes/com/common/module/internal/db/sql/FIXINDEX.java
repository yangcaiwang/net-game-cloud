
package com.common.module.internal.db.sql;

import com.common.module.internal.db.entity.DBEntity;

public class FIXINDEX {

	public static String BUILDADDPRIMARYKEYSQL(String table, String field) {

		String sql = "alter table `" + table + "` add primary key(`" + field + "`)";
		return sql;
	}

	public static String BUILDADDUNIQUESQL(String table, String field, String usingType) {

		String sql = "alter table `" + table + "` add unique index(`" + field + "`) using " + usingType;
		return sql;
	}

	public static String BUILDADDNORMALINDEXSQL(String table, String indexName, String field, String usingType) {

		String sql = "alter table `" + table + "` add index " + indexName + " (`" + field + "`) using " + usingType;
		return sql;
	}

	public static <T extends DBEntity> String BUILDADDFULLTEXTINDEXSQL(String table, String field, String usingType) {

		String sql = "alter table `" + table + "` add fulltext index" + " (`" + field + "`) using " + usingType;
		return sql;
	}
}
