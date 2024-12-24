
package com.common.module.internal.db.annotation;

import java.lang.annotation.*;

/**
 * MySQL数据库,如果使用myisam引擎,mysql版本必须是6.0以下,,utf8mb4编码
 * utf8mb4对应的排序字符集有utf8mb4_unicode_ci、utf8mb4_general_ci.
 * 
 * utf8mb4_unicode_ci和utf8mb4_general_ci的对比：
 * 
 * 准确性： utf8mb4_unicode_ci是基于标准的Unicode来排序和比较，能够在各种语言之间精确排序
 * utf8mb4_general_ci没有实现Unicode排序规则，在遇到某些特殊语言或者字符集，排序结果可能不一致。
 * 但是，在绝大多数情况下，这些特殊字符的顺序并不需要那么精确。 性能 utf8mb4_general_ci在比较和排序的时候更快
 * utf8mb4_unicode_ci在特殊情况下，Unicode排序规则为了能够处理特殊字符的情况，实现了略微复杂的排序算法。
 * 但是在绝大多数情况下发，不会发生此类复杂比较。相比选择哪一种collation，使用者更应该关心字符集与排序规则在db里需要统一。
 * 
 * 测试环境用mysql5.0版本,使用myisam引擎提高性能,云数据库只能使用innodb
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface DB {

	int ORACLE_NAME_MAX_LEN = 30;

	/**
	 * 库名,表名,字段名,索引名最大长度
	 * </p>
	 * MySQL=64
	 * </p>
	 * Oracle = 30
	 * </p>
	 * SQL SERVER = 128
	 * </p>
	 * 
	 */
	int MySQL_NAME_MAX_LEN = ORACLE_NAME_MAX_LEN;

	/** 默认使用的驱动 */
	String DRIVERCLASSNAME = "com.mysql.jdbc.Driver";

	/** mysql-connector-java 8.0驱动默认使用的驱动 */
	String DRIVERCLASSNAME_80 = "com.mysql.cj.jdbc.Driver";

	/** 自动创建数据库所依赖的系统库 */
	String HELP_DB = "mysql";

	// 编码
	String CHARSET_UTF8MB4 = "utf8mb4";
	String CHARSET_UTF8 = "utf8";

	// 校队方式 utf8mb4_general_ci or utf8mb4_unicode_ci
	String COLLATE_UTF8MB4 = "utf8mb4_general_ci";
	// utf8_general_ci or utf8_unicode_ci
	String COLLATE_UTF8 = "utf8_general_ci";

	// 库
	String DB_GAME_ALIAS = "db_game"; // 游戏库
	String DB_LOG_ALIAS = "db_log"; // 日志库
	String DB_STATISTIC_ALIAS = "db_statistic"; // 统计用的数据库
	String DB_CENTER = "db_center"; // 中心库
	String DB_BACKSTAGE = "db_backstage";// 后台

	// 引擎

	/**
	 * 不支持事务,读取性能高,支持联合主键带自增列,mysql8.0以后已经彻底放弃
	 */
	String ENGINE_MYISAM = "myisam";
	/**
	 * 支持事务
	 */
	String ENGINE_INNODB = "innodb";

	/**
	 * 数据库别名，必须小写
	 * 
	 * @return
	 */
	String aliasName() default DB_GAME_ALIAS;

	/**
	 * 数据库引擎 myisam or innodb
	 * 
	 * @return
	 */
	String engine() default ENGINE_INNODB;

	/**
	 * 默认编码 utf8 or utf8mb4
	 * 
	 * @return
	 */
	String charset() default CHARSET_UTF8MB4;

	/**
	 * 校队方式
	 * </p>
	 * "utf8mb4_general_ci" or "utf8mb4_unicode_ci"
	 * </p>
	 * "utf8_general_ci" or "utf8_unicode_ci"
	 * 
	 * @return
	 */
	String collate() default COLLATE_UTF8MB4;
}
