
package com.common.module.internal.db.dao;

import com.common.module.util.AnnotationUtil;
import com.common.module.internal.db.annotation.Persistent;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;
import com.common.module.util.ArraysUtils;
import com.common.module.util.StringUtils;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

public final class Wrapper {

	private Wrapper() {

	}

	// 改成单例，避免产生过多对象
	private static volatile Wrapper instance;
	public static Wrapper getInstance() {
		if (instance == null) {
			synchronized (Wrapper.class) {
				if (instance == null) {
					instance = new Wrapper();
				}
			}
		}
		return instance;
	}

	public void wrap(PreparedStatement pst, Serializable... keys) throws Exception {
		for (int i = 1; i <= keys.length; i++) {
			try {
				pst.setObject(i, keys[i - 1]);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage() + ":" + StringUtils.toString(keys) + "," + i + "," + pst.getUpdateCount() + "," + pst, e);
			}
		}
	}

	public void wrap(PreparedStatement pst, DBEntity obj) throws Exception {
		LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(obj.getEntityType());
		int i = 1;
		for (Field field : fields)
			wrap(pst, obj, field, i++);
	}

	public int wrap(PreparedStatement pst, DBEntity obj, LinkedHashSet<Field> fields, int i) throws Exception {
		int j = i;
		for (Field field : fields)
			wrap(pst, obj, field, j++);
		return j;
	}

	public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
		try {
			((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class)).dataType().wrap(pst, obj, field, i);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + ":" + field + "," + i + "," + pst.getUpdateCount() + "," + pst + "," + obj, e);
		}
	}

	// --------------------------------------------------------------------------------------------------------------------------------------------------------------//
	public void wrap(ResultSet rs, DBEntity obj) throws Exception {
		wrap(rs, obj, null);
	}

	/**
	 * 自定义从mysql数据结果集中解析数据
	 * 
	 * @param rs
	 *            结果集
	 * @param obj
	 *            目标对象,解析出来的数据赋值给它
	 * @throws Exception
	 */
	public void wrap(ResultSet rs, DBEntity obj, Predicate<String> columnFilter) throws Exception {
		LinkedHashSet<Field> fieldSet = DBEntityUtils.fieldSet(obj.getEntityType());
		for (Field field : fieldSet) {
			if (columnFilter == null) {
				wrap(obj, rs, field);
			} else {
				if ((!columnFilter.test(DBEntityUtils.columnName(field))))
					wrap(obj, rs, field);
			}
		}
	}

	public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
		try {
			((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class)).dataType().wrap(obj, rs, field);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + ":" + field + "," + rs.getCursorName() + "," + rs + "," + obj, e);
		}
	}

	/**
	 * 真实的sql
	 * 
	 * @param sql
	 * @param maps
	 *            参数map
	 * @return
	 */
	String buildSQL(String sql, LinkedHashMap<Field, Object>... maps) {
		String sql_ = new StringBuilder().append(sql).toString();
		String _sql = sql_.replaceAll("\\?", "\\%s");
		LinkedList<Object> values = Lists.newLinkedList();
		for (int i = 0; i < maps.length; i++) {
			values.addAll(maps[i].values());
		}
		Object[] params = ArraysUtils.toArray(values, Object.class);
		for (int i = 0; i < params.length; i++) {
			String v = "'" + params[i] + "'";
			params[i] = v;
		}
		return String.format(_sql, params);
	}

}
