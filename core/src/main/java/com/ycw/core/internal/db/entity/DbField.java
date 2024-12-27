
package com.ycw.core.internal.db.entity;

import com.ycw.core.internal.db.annotation.Index;
import com.ycw.core.internal.db.annotation.PK;
import com.ycw.core.internal.db.annotation.Persistent;
import com.ycw.core.internal.db.constant.DataType;
import com.ycw.core.util.AnnotationUtil;
import com.ycw.core.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * <db实体字段类>
 * <p>
 * ps: 数据库存储的字段模版信息
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class DbField implements Serializable {

	public static final String PRI = "PRI";// 主键

	public static final String UNI = "UNI";// 唯一索引

	public static final String MUL = "MUL";// 普通索引

	public static final String NULL = "YES";

	public static final String NOT_NULL = "NO";

	public final String field; // 字段

	public final String type; // 类型

	public final String nil; // 是否允许为空

	public final String key; // key类型,主键
	// or
	// 唯一索引
	// or
	// 普通索引

	public final String def; // 默认值

	/**
	 * extra列中出现的信息一般不是太重要，但是还是有很多信息我们可以从这里面获取到： using
	 * index：出现这个说明mysql使用了覆盖索引，避免访问了表的数据行，效率不错！ using
	 * where：这说明服务器在存储引擎收到行后将进行过滤。有些where中的条件会有属于索引的列，当它读取使用索引的时候，就会被过滤，
	 * 所以会出现有些where语句并没有在extra列中出现using where这么一个说明。 using
	 * temporary：这意味着mysql对查询结果进行排序的时候使用了一张临时表。 using
	 * filesort：这个说明mysql会对数据使用一个外部的索引排序，而不是按照表内的索引顺序进行读取
	 */
	public final String extra;

	public DbField(String field, String type, String nil, String key, String def, String extra) {
		super();
		this.field = field;
		this.type = type;
		this.nil = nil;
		this.key = key;
		this.def = def;
		this.extra = extra;
	}

	public boolean isSameTo(Field javaField, Class<? extends DBEntity> entityType) {

		if (null == javaField) {
			return false;
		}
		Persistent persistent = AnnotationUtil.findAnnotation(javaField, Persistent.class);
		DataType javaDataType = persistent.dataType();
		int javaLen = persistent.len() == 0 ? javaDataType.len : persistent.len();
		int javaScale = persistent.scale() == 0 ? javaDataType.scale : persistent.scale();
		int javaFloat = javaScale;
		int javaDouble = javaScale;
		boolean javaCanBeNull = javaDataType.canBeNull(javaField);
		PK javaPk = AnnotationUtil.findAnnotation(javaField, PK.class);
		Index javaIndex = AnnotationUtil.findAnnotation(javaField, Index.class);
		String javaIndexName = null;
		String javaKey = "";
		if (javaPk != null) {
			javaKey = PRI;
		} else if (javaIndex != null) {
			javaIndexName = javaIndex.name();
			if (javaIndex.type().equals(Index.INDEX_TYPE_UNIQUE)) {
				javaKey = UNI;
			} else {
				javaKey = MUL;
			}
		}
		String db_column_name = this.field;
		String db_type_name = this.type;
		String db_null_able = this.nil;
		String db_key = this.key;
		String java_column_name = DBEntityUtils.columnName(javaField);
		String java_type_name = javaDataType.dbType + "%s";
		String java_len_name = javaDataType == DataType.DECIMAL ? "(" + javaLen + "," + javaDouble + ")" : javaLen == 0 ? "" : javaDataType == DataType.DOUBLE ? "(" + javaLen + "," + javaDouble + ")" : javaDataType == DataType.FLOAT ? "(" + javaLen + "," + javaFloat + ")" : "(" + javaLen + ")";
		java_type_name = String.format(java_type_name, java_len_name);
		String java_null_able = javaCanBeNull ? NULL : NOT_NULL;
		boolean is_same = db_column_name.toLowerCase().equals(java_column_name.toLowerCase()) && db_type_name.toLowerCase().equals(java_type_name.toLowerCase()) && db_null_able.toLowerCase().equals(java_null_able.toLowerCase());
		return is_same;
	}

	@Override
	public String toString() {
		return StringUtils.toString(this);
	}
}
