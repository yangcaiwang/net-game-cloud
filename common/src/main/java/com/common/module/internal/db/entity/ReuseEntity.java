package com.common.module.internal.db.entity;

import com.common.module.internal.db.annotation.*;
import com.common.module.internal.db.constant.DataType;

import java.util.concurrent.ConcurrentLinkedQueue;

@Deprecated
@DB(aliasName = DB.DB_LOG_ALIAS)
@Table(name = "t_reuse", comment = "重用id汇总", mappedSuperclass = false, cached = @Cached(delay = 8, liveDuration = Cached.NEVER_EXPIRE))
public class ReuseEntity extends DBEntity {

    @PK
    @Persistent(name = "table_name", dataType = DataType.STRING, len = DB.MySQL_NAME_MAX_LEN, comment = "数据表名称")
    private String tabName;

    @Persistent(name = "reuse_id_queue", dataType = DataType.OBJECT, comment = "可重用的id队列")
    private ConcurrentLinkedQueue<Long> reuseIdQueue = new ConcurrentLinkedQueue<>();

    public boolean hasReuseId() {
	return !reuseIdQueue.isEmpty();
    }

    public boolean addReuseId(Long id) {
	boolean add = false;
	try {
	    return add = reuseIdQueue.contains(id) ? false : reuseIdQueue.offer(id);
	} finally {
	    if (add)
		log.info("收集到废弃主键:" + id + "," + tabName + "," + reuseIdQueue.size());
	}
    }

    public Long getReuseId() {
	Long pk = null;
	try {
	    return pk = reuseIdQueue.poll();
	} finally {
	    if (pk != null)
		log.info("重用主键:" + pk + "," + tabName + "," + reuseIdQueue.size());
	}
    }

    public String getTabName() {
	return tabName;
    }

    public void setTabName(String tabName) {
	this.tabName = tabName;
    }

    public ConcurrentLinkedQueue<Long> getReuseIdQueue() {
	return reuseIdQueue;
    }

    public void setReuseIdQueue(ConcurrentLinkedQueue<Long> reuseIdQueue) {
	this.reuseIdQueue = reuseIdQueue;
    }

    @Override
    public String mergeRoleKey() {
        return null;
    }
}
