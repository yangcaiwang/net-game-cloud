
package com.common.module.internal.db.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface DB {

    /**
     * 库名,表名,字段名,索引名最大长度
     */
    int MySQL_NAME_MAX_LEN = 30;

    /**
     * 默认使用的驱动
     */
    String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    /**
     * mysql-connector-java 8.0驱动默认使用的驱动
     */
    String DRIVER_CLASSNAME_80 = "com.mysql.cj.jdbc.Driver";

    /**
     * 自动创建数据库所依赖的系统库
     */
    String HELP_DB = "mysql";

    /**
     * 编码
     */
    String CHARSET_UTF8MB4 = "utf8mb4";

    String COLLATE_UTF8MB4 = "utf8mb4_general_ci";

	/**
	 * 游戏库
	 */
    String DB_GAME_ALIAS = "db_game";

	/**
	 * 日志库
	 */
    String DB_LOG_ALIAS = "db_log";

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
     */
    String aliasName() default DB_GAME_ALIAS;

    /**
     * 数据库引擎 myisam or innodb
     */
    String engine() default ENGINE_INNODB;

    /**
     * 默认编码 utf8 or utf8mb4
     */
    String charset() default CHARSET_UTF8MB4;

    /**
     * 校队方式
     */
    String collate() default COLLATE_UTF8MB4;
}
