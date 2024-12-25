
package com.common.module.internal.db.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface Cached {

	/**
	 * 长期有效，永远不过期
	 */
	int NEVER_EXPIRE = -1;

	/**
	 * 本地缓存延数据延迟入库任务迭代时间-秒
	 * 
	 * @return
	 */
	int delay() default 60;

	/**
	 * 单次入库最多条数
	 * </p>
	 * 理论上批量保存1000条性能较优
	 * </p>
	 * 但是由于业务上使用了大量的复合字段,造成单条数据太大
	 * </p>
	 * 容易出Packet for query is too large异常
	 * </p>
	 * 所以这里需要根据自身业务数据量大小来调整
	 * </p>
	 * </p>
	 * 可以使用命令 set global max_allowed_packet = ?(1024*1024*1024);
	 * 临时修改mysql交互数据包大小
	 * </p>
	 * 查看当前安装的mysql 数据包大小设定 : show VARIABLES like '%max_allowed_packet%';
	 * </p>
	 * 修改my.cnf max_allowed_packet = ?(1024*1024*1024);永久生效mysql交互数据包大小
	 * </p>
	 * 如果出现数据太大,针对自己模块实体的结构调整这个批量的数量或者修改mysql配置
	 * </p>
	 */
	int limit() default 1000;

	/** 本地缓存数据存活持续时间-秒 */
	int liveDuration() default 1800;

	/** 并发级别,也就是缓存hash的桶的数量 */
	int concurrentLevel() default 16;

	/** 最大缓存对象个数 */
	int maximumSize() default 1000000;

	boolean readNotSave() default false;

	/**
	 * 缓存操作类型
	 * 
	 * @author ljs
	 *
	 */
	enum Options {
		/** 实体加入缓存的时间 */
		CREATE,
		/** 访问-get时间 */
		READ,
		/** 修改-put时间 */
		UPDATE,;
	}

}
