
package com.ycw.core.internal.db.dao;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ycw.core.internal.db.Mysql;
import com.ycw.core.internal.db.annotation.DB;
import com.ycw.core.internal.db.constant.SQLType;
import com.ycw.core.internal.db.entity.DBEntity;
import com.ycw.core.internal.db.entity.DBEntityUtils;
import com.ycw.core.internal.db.entity.DbField;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

/**
 * <DAO操作接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface IDao {

	/**
	 * 根据主键构建实体对象
	 * 
	 * @param entityType
	 * @param pks
	 * @return
	 */
	default <T extends DBEntity> T newEntityInstance(Class<T> entityType, Serializable... pks) {
		return DBEntityUtils.newEntityInstance(entityType);
	}

	/**
	 * @param entityType
	 *            数据库别名
	 * 
	 * @param entityType
	 * @return
	 */
	default <T extends DBEntity> String aliasName(Class<T> entityType) {
		return Mysql.getInstance().aliasName(entityType);
	}

	/**
	 * 简洁表名-必须小写
	 * 
	 * @param entityType
	 * @return
	 */
	default <T extends DBEntity> String simpleTableName(Class<T> entityType) {
		return DBEntityUtils.simpleTableName(entityType);
	}

	/**
	 * 分表数量
	 * 
	 * @param entityType
	 * @return
	 */
	default <T extends DBEntity> int tableNum(Class<T> entityType) {
		return DBEntityUtils.tableNum(entityType);
	}

	/**
	 * 自动组装的sql语句
	 * 
	 * @param entityType
	 * @param sqlType
	 * @return
	 */
	default <T extends DBEntity> String buildSQL(Class<T> entityType, SQLType sqlType, String tableName) {
		return Mysql.getInstance().buildSQL(entityType, sqlType, tableName);
	}

	default boolean useInnodb() {
		return System.getProperty("mysql.engine", DB.ENGINE_INNODB).equalsIgnoreCase(DB.ENGINE_INNODB);
	}

	default boolean useMyIsam() {
		return System.getProperty("mysql.engine", DB.ENGINE_INNODB).equalsIgnoreCase(DB.ENGINE_MYISAM);
	}

	/**
	 * 打开mysql连接
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @return
	 */
	default Connection open(String aliasName) {

		return Mysql.getInstance().open(aliasName);
	}

	/** 关闭mysql连接 */
	default <T extends Statement> boolean close(Connection conn, Collection<T> statements, Collection<ResultSet> rss) {

		return Mysql.getInstance().close(conn, statements, rss);
	}

	/** 关闭mysql连接 */
	default <T extends Statement> boolean close(Connection conn, T statement, ResultSet rs) {

		return Mysql.getInstance().close(conn, statement, rs);
	}

	/**
	 * 创建数据库,</br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 
	 * @param conn
	 * @param realName
	 *            真实数据库名
	 * @param charset
	 * @param collate
	 * @return
	 */
	Boolean createDatabase(Connection conn, String realName, String charset, String collate) throws SQLException;

	/**
	 * 创建数据库</br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 
	 * @param realName
	 *            真实数据库名
	 * @param charset
	 * @param collate
	 * @return
	 */
	Boolean createDatabase(String realName, String charset, String collate) throws SQLException;

	/**
	 * 创建数据库</br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 
	 * @param realName
	 *            真实数据库名
	 * @return
	 */
	Boolean createDatabase(String realName) throws SQLException;

	/** 显示所有库名 */
	Set<String> showDatabases();

	/** 显示所有包含like的库名 */
	Set<String> showDatabases(String like);

	/** 显示所有库名 */
	Set<String> showDatabases(Connection conn);

	/** 显示所有包含like的库名 */
	Set<String> showDatabases(Connection conn, String like);

	Boolean existsDatabase(String realName);

	Boolean existsTable(String realName, String tableName);

	Boolean existsColumn(String realName, String tableName, String column);

	/**
	 * 列出全部包含like的表
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param like
	 * @return
	 */
	Set<String> showTables(String aliasName, String like);

	/**
	 * 列出全部的表
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @return
	 */
	Set<String> showTables(String aliasName);

	/**
	 * 列出class的所有字段对应的mysql模版信息,如果是滚表数据会得到多个实例 {table:[columns]}
	 * 
	 * @param entityType
	 * @return
	 */
	<T extends DBEntity> Map<String, LinkedHashSet<DbField>> showTablesDbFiledList(Class<T> entityType);

	DbField getColumn(String aliasName, String tableName, String column);

	/**
	 * 把旧表的所有字段类型都复制到新表
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param newTableName
	 * @param oldTableName
	 * @return
	 */
	Boolean copyTable(String aliasName, String newTableName, String oldTableName);

	<T extends DBEntity> Boolean createTableIfNotExists(Class<T> entityType, String aliasName, String tableName);

	Boolean cleanTable(String aliasName, String tableName);

	Boolean truncateTable(String aliasName, String tableName);

	Boolean dropTable(String aliasName, String tableName);

	/** 插入一条数据到数据库并且返回得到的自增ID */
	<T extends DBEntity> Long returnGeneratedKeys(T obj);

	<T extends DBEntity> Boolean insert(Collection<T> c, Class<T> entityType, String aliasName, String tableName);

	<T extends DBEntity> Boolean insertOrUpdate(Collection<T> c, Class<T> entityType, String aliasName, String tableName);

	<T extends DBEntity> Boolean insertIfNotExists(Collection<T> c, Class<T> entityType, String aliasName, String tableName);

	<T extends DBEntity> Boolean replace(Collection<T> c, Class<T> entityType, String aliasName, String tableName);

	<T extends DBEntity> boolean delete(Collection<T> c, Class<T> entityType, String aliasName, String tableName);

	<T extends DBEntity> Boolean update(Collection<T> c, Class<T> entityType, String aliasName, String tableName);

	/** 插入数据库 */
	<T extends DBEntity> Boolean insert(Collection<T> c, Class<T> entityType);

	/** 插入数据库，主键重复就执行更新 */
	<T extends DBEntity> Boolean insertOrUpdate(Collection<T> c, Class<T> entityType);

	/** 如果不存在就插入 */
	<T extends DBEntity> Boolean insertIfNotExists(Collection<T> c, Class<T> entityType);

	/** 插入如果已存在就替换 */
	<T extends DBEntity> Boolean replace(Collection<T> c, Class<T> entityType);

	/** 删除表里主键为keys的那一条数据 */
	<T extends DBEntity> Boolean delete(Class<T> entityType, Serializable... pks);

	/** 删除多个主键 **/
	<T extends DBEntity> Boolean deleteByKeys(Class<T> entityType, String... pks);

	/** 删除表里满足条件condition的数据 */
	<T extends DBEntity> Boolean delete(Class<T> entityType, String condition);

	/** 批量删除 */
	<T extends DBEntity> Boolean delete(Collection<T> c, Class<T> entityType);

	/** 更新 */
	<T extends DBEntity> Boolean update(Collection<T> c, Class<T> entityType);

	default <T extends DBEntity> Boolean insert(Class<T> entityType, T... objs) {

		return insert(Lists.newArrayList(objs), entityType);
	}

	default <T extends DBEntity> Boolean insertOrUpdate(Class<T> entityType, T... objs) {

		return insertOrUpdate(Lists.newArrayList(objs), entityType);
	}

	default <T extends DBEntity> Boolean insertIfNotExists(Class<T> entityType, T... objs) {

		return insertIfNotExists(Lists.newArrayList(objs), entityType);
	}

	default <T extends DBEntity> Boolean replace(Class<T> entityType, T... objs) {

		return replace(Lists.newArrayList(objs), entityType);
	}

	default <T extends DBEntity> Boolean delete(Class<T> entityType, T... objs) {

		return delete(Lists.newArrayList(objs), entityType);
	}

	default <T extends DBEntity> Boolean update(Class<T> entityType, T... objs) {

		return update(Lists.newArrayList(objs), entityType);
	}

	/**
	 * 获取数据
	 * 
	 * @param entityType
	 *            实体类型
	 * @param sql
	 *            自定义sql语句
	 * @return
	 */
	<T extends DBEntity> List<Map<String, Object>> query(Class<T> entityType, String sql);

	/**
	 * 获取数据
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param sql
	 *            自定义sql语句
	 * @return
	 */
	<T extends DBEntity> List<Map<String, Object>> query(String aliasName, String sql);

	/** 获取全部数据,业务中如果有需求要查询全部数据的话,先查看下数据条数是否过多然后再考虑是否需要分批查询 */
	<T extends DBEntity> List<T> queryAll(Class<T> entityType);

	/** 获取全部数据,业务中如果有需求要查询全部数据的话,先查看下数据条数是否过多然后再考虑是否需要分批查询 */
	<T extends DBEntity> List<T> queryAll(Class<T> entityType, String aliasName, String tableName);

	/** 获取指定区间数据 */
	<T extends DBEntity> List<T> queryLimit(Class<T> entityType, String aliasName, String tableName, long beginLimit, long endLimit);

	/** 获取主键为pks的一条数据 */
	<T extends DBEntity> T query(Class<T> entityType, Serializable... pks);

	/** 获取主键为pks的一条数据,指定库和表 */
	<T extends DBEntity> T query(Class<T> entityType, String aliasName, String tableName, Serializable... pks);

	/** 获取主键为pksArray的一批数据 */
	<T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, Serializable[]... pksArray);

	/** 获取主键为pksArray的一批数据,指定库和表 */
	<T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, String aliasName, String tableName, Serializable[]... pksArray);

	/** 获取主键为pksList的一批数据 */
	<T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, List<Serializable[]> pksList);

	/** 获取主键为pksList的一批数据,指定库和表 */
	<T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, String aliasName, String tableName, List<Serializable[]> pksList);

	/** 直接通过一批主键(必须是唯一整数型(byte,short,int,long),不包含浮点和Decmal),都统一转成long去操作 */
	<T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, Collection<Long> keys);

	/** 获取主键为keySet的一批数据,必须只有一个long型主键,指定库和表 */
	<T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, String aliasName, String tableName, Collection<Long> keys);

	/** 获取满足条件condition的所有数据 */
	<T extends DBEntity> List<T> queryWithCondition(Class<T> entityType, String condition);

	/** 获取满足条件condition的所有数据 ,指定库和表 */
	<T extends DBEntity> List<T> queryWithCondition(Class<T> entityType, String aliasName, String tableName, String condition);

	/**
	 * 自定义查询数据
	 * 
	 * @param entityType
	 *            class实体类型
	 * @param sql
	 *            自定义的sql语句
	 * @param query
	 *            执行的接口
	 */
	<T extends DBEntity> void query(Class<T> entityType, String sql, Query query);

	/**
	 * 自定义查询数据
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param sql
	 *            自定义的sql语句
	 * @param query
	 *            执行的接口
	 */
	void query(String aliasName, String sql, Query query);

	/**
	 * 批量执行sql语句，使用Statement，适用一次性执行</br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param sqls
	 * @return
	 */
//	int[] execute(String aliasName, String... sqls) throws SQLException;

	/**
	 * 批量执行sql语句，使用Statement ，适用一次性执行</br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param sqls
	 * @return
	 */
	int[] execute(String aliasName, Collection<String> sqls) throws SQLException;

	/**
	 * 执行一条sql语句，使用PreparedStatement ，适用多次执行</br>
	 * </br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 方法executeUpdate :</br>
	 * 用于执行 INSERT、UPDATE 或 DELETE 语句以及 SQL DDL（数据定义语言）语句，例如 CREATE TABLE 和
	 * DROPTABLE。INSERT、UPDATE 或 DELETE</br>
	 * 语句的效果是修改表中零行或多行中的一列或多列。</br>
	 * executeUpdate 的返回值是一个整数（int），指示受影响的行数（即更新计数）。对于 CREATETABLE 或 DROP TABLE
	 * 等不操作行的语句，executeUpdate 的返回值总为零。</br>
	 * 方法execute :</br>
	 * 可用于执行任何SQL语句，返回一个boolean值，表明执行该SQL语句是否返回了ResultSet。如果执行后第一个结果是ResultSet，
	 * 则返回true，否则返回false</br>
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	Integer executeUpdate(String aliasName, String sql) throws SQLException;

	/**
	 * 执行一条sql语句，使用PreparedStatement ，适用多次执行</br>
	 * </br>
	 * 其实这里抛异常外层也不能怎么样,</br>
	 * 但是由于sql执行executeUpdate没有行,列发生变化返回0,</br>
	 * 如果执行execute,那么如果resultSet没有结果返回false,</br>
	 * 所以不能确定是否执行成功,所以把失败的异常抛出来</br>
	 * 方法executeUpdate :</br>
	 * 用于执行 INSERT、UPDATE 或 DELETE 语句以及 SQL DDL（数据定义语言）语句，例如 CREATE TABLE 和
	 * DROPTABLE。INSERT、UPDATE 或 DELETE</br>
	 * 语句的效果是修改表中零行或多行中的一列或多列。</br>
	 * executeUpdate 的返回值是一个整数（int），指示受影响的行数（即更新计数）。对于 CREATETABLE 或 DROP TABLE
	 * 等不操作行的语句，executeUpdate 的返回值总为零。</br>
	 * 方法execute :</br>
	 * 可用于执行任何SQL语句，返回一个boolean值，表明执行该SQL语句是否返回了ResultSet。如果执行后第一个结果是ResultSet，
	 * 则返回true，否则返回false</br>
	 * 
	 *
	 * @param aliasName
	 *            数据库别名
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	Boolean execute(String aliasName, String sql) throws SQLException;

	/**
	 * 获取数据源的一些信息
	 * 
	 * @param conn
	 * @return
	 */
	default DatabaseMetaData getDatabaseMetaData(Connection conn) {

		try {
			return conn.getMetaData();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * @param conn
	 * @param catalog
	 *            null
	 * @param schemaPattern
	 *            null
	 * @param tableNamePattern
	 *            大写table名称
	 * @param types
	 *            [TABLE | VIEW]
	 * @return </br>
	 *         LinkedHashMap{</br>
	 *         [TABLE_CAT, db_game_test];</br>
	 *         [TABLE_SCHEM, null];</br>
	 *         [TABLE_NAME, player];</br>
	 *         [TABLE_TYPE, TABLE];</br>
	 *         [REMARKS, ];</br>
	 *         [TYPE_CAT, null];</br>
	 *         [TYPE_SCHEM, null];</br>
	 *         [TYPE_NAME, null];</br>
	 *         [SELF_REFERENCING_COL_NAME, null];</br>
	 *         [REF_GENERATION, null];</br>
	 *         }</br>
	 */
	default List<Map<String, Object>> getTablesMetaData(Connection conn, String catalog, String schemaPattern, String tableNamePattern, String types[]) {

		try {
			List<Map<String, Object>> list = Lists.newArrayList();
			DatabaseMetaData databaseMetaData = getDatabaseMetaData(conn);
			ResultSet rs = databaseMetaData.getTables(catalog, schemaPattern, tableNamePattern, types);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs != null && rs.next()) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				for (int i = 0; ++i <= meta.getColumnCount();) {
					String name = meta.getColumnLabel(i);
					Object value = rs.getObject(name);
					map.put(name, value);
				}
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
	}

	/**
	 * @param conn
	 * @param catalog
	 *            null
	 * @param schema
	 *            null
	 * @param TABLE_NAME
	 *            大写table名称
	 * @return</br>
	 * 				LinkedHashMap{</br>
	 *              [TABLE_CAT, db_game_test];</br>
	 *              [TABLE_SCHEM, null];</br>
	 *              [TABLE_NAME, PLAYER];</br>
	 *              [COLUMN_NAME, id];</br>
	 *              [KEY_SEQ, 1];</br>
	 *              [PK_NAME, PRIMARY];</br>
	 *              }</br>
	 */
	default List<Map<String, Object>> getPrimaryKeysMetaData(Connection conn, String catalog, String schema, String TABLE_NAME) {

		try {
			List<Map<String, Object>> list = Lists.newArrayList();
			DatabaseMetaData databaseMetaData = getDatabaseMetaData(conn);
			ResultSet rs = databaseMetaData.getPrimaryKeys(catalog, schema, TABLE_NAME);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs != null && rs.next()) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				for (int i = 0; ++i <= meta.getColumnCount();) {
					String name = meta.getColumnLabel(i);
					Object value = rs.getObject(name);
					map.put(name, value);
				}
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
	}

	/**
	 * @param conn
	 * @param catalog
	 *            null
	 * @param schema
	 *            null
	 * @param TABLE_NAME
	 *            null or 大写table名称
	 * @param unique
	 *            是否唯一索引
	 * @param approximate
	 *            如果为true，则允许结果反映数据值中的近似值； 如果为假，则要求结果准确
	 * @return</br>
	 * 				LinkedHashMap{</br>
	 *              [TABLE_CAT, db_game_test];</br>
	 *              [TABLE_SCHEM, null];</br>
	 *              [TABLE_NAME, player];</br>
	 *              [NON_UNIQUE, true];</br>
	 *              [INDEX_QUALIFIER, ];</br>
	 *              [INDEX_NAME, idx_crt_time];</br>
	 *              [TYPE, 3];</br>
	 *              [ORDINAL_POSITION, 1];</br>
	 *              [COLUMN_NAME, crt_time];</br>
	 *              [ASC_OR_DESC, A];</br>
	 *              [CARDINALITY, 42];</br>
	 *              [PAGES, 0];</br>
	 *              [FILTER_CONDITION, null];</br>
	 *              }
	 */
	default List<Map<String, Object>> getIndexesMetaData(Connection conn, String catalog, String schema, String TABLE_NAME, boolean unique, boolean approximate) {

		try {
			List<Map<String, Object>> list = Lists.newArrayList();
			DatabaseMetaData databaseMetaData = getDatabaseMetaData(conn);
			ResultSet rs = databaseMetaData.getIndexInfo(catalog, schema, TABLE_NAME, unique, approximate);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs != null && rs.next()) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				for (int i = 0; ++i <= meta.getColumnCount();) {
					String name = meta.getColumnLabel(i);
					Object value = rs.getObject(name);
					map.put(name, value);
				}
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
	}

	/**
	 * @param conn
	 * @param catalog
	 *            null
	 * @param schemaPattern
	 *            null
	 * @param tableNamePattern
	 *            大写table名称
	 * @param columnNamePattern
	 *            null or a column name pattern
	 * @return</br>
	 * 				LinkedHashMap{</br>
	 *              [TABLE_CAT, db_game_test];</br>
	 *              [TABLE_SCHEM, null];</br>
	 *              [TABLE_NAME, player];</br>
	 *              [COLUMN_NAME, open_functions];</br>
	 *              [DATA_TYPE, -1];</br>
	 *              [TYPE_NAME, TEXT];</br>
	 *              [COLUMN_SIZE, 65535];</br>
	 *              [BUFFER_LENGTH, 65535];</br>
	 *              [DECIMAL_DIGITS, null];</br>
	 *              [NUM_PREC_RADIX, 10];</br>
	 *              [NULLABLE, 1];</br>
	 *              [REMARKS, 已开放的功能列表];</br>
	 *              [COLUMN_DEF, null];</br>
	 *              [SQL_DATA_TYPE, 0];</br>
	 *              [SQL_DATETIME_SUB, 0];</br>
	 *              [CHAR_OCTET_LENGTH, 65535];</br>
	 *              [ORDINAL_POSITION, 21];</br>
	 *              [IS_NULLABLE, YES];</br>
	 *              [SCOPE_CATALOG, null];</br>
	 *              [SCOPE_SCHEMA, null];</br>
	 *              [SCOPE_TABLE, null];</br>
	 *              [SOURCE_DATA_TYPE, null];</br>
	 *              [IS_AUTOINCREMENT, NO];</br>
	 *              [IS_GENERATEDCOLUMN, NO];</br>
	 *              }</br>
	 */
	default List<Map<String, Object>> getColumnsMetaData(Connection conn, String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {

		try {
			List<Map<String, Object>> list = Lists.newArrayList();
			DatabaseMetaData databaseMetaData = getDatabaseMetaData(conn);
			ResultSet rs = databaseMetaData.getColumns(catalog, schemaPattern, tableNamePattern, columnNamePattern);
			ResultSetMetaData meta = rs.getMetaData();
			while (rs != null && rs.next()) {
				Map<String, Object> map = Maps.newLinkedHashMap();
				for (int i = 0; ++i <= meta.getColumnCount();) {
					String name = meta.getColumnLabel(i);
					Object value = rs.getObject(name);
					map.put(name, value);
				}
				list.add(map);
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
	}
}
