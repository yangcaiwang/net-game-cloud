package com.common.module.internal.db.entity;

import com.common.module.internal.db.annotation.*;
import com.common.module.internal.db.constant.DataType;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 使用分表策略时，用这个表来生成和保存分表的全局最大值，如果合区需要由小到大，确保id值更大不会重复
 *
 */
@Deprecated
@DB(aliasName = DB.DB_LOG_ALIAS)
@Table(name = "t_global_ids", mappedSuperclass = false, comment = "分表策略全局最大id记录表", cached = @Cached(delay = 10, liveDuration = Cached.NEVER_EXPIRE))
public class GlobalIdEntity extends DBEntity {

	@PK
	@Persistent(name = "table_name", dataType = DataType.STRING, len = DB.MySQL_NAME_MAX_LEN, comment = "数据表名称")
	private String tabName;

	@Persistent(name = "db_name", dataType = DataType.STRING, len = DB.MySQL_NAME_MAX_LEN, comment = "数据库名称")
	private String dbName;

	@Persistent(name = "max_id", dataType = DataType.LONG, comment = "id最大值")
	private AtomicLong maxId = new AtomicLong();

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public long getMaxId() {
		return maxId.get();
	}

	public void setMaxId(long maxId) {
		this.maxId.compareAndSet(this.maxId.get(), maxId);
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public long nextId() {
		return this.maxId.incrementAndGet();
	}

	@Override
	public String mergeRoleKey() {
		return null;
	}
}
