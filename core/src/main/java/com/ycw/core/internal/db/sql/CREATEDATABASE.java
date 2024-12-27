
package com.ycw.core.internal.db.sql;

import com.ycw.core.internal.db.annotation.DB;

public final class CREATEDATABASE {

	public static String BUILDSQL(String dbName, String charset, String collate) {

		return String.format("create database if not exists %s default charset %s collate %s", dbName, charset, collate);
	}

	public static String BUILDSQL(String dbName) {

		return BUILDSQL(dbName, DB.CHARSET_UTF8MB4, DB.COLLATE_UTF8MB4);
	}
}
