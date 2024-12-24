
package com.common.module.internal.db.entity;

import com.common.module.internal.db.annotation.PK;
import com.common.module.internal.db.annotation.Persistent;
import com.common.module.internal.db.constant.DataType;

import java.io.Serializable;
import java.util.Date;

/**
 * 自增主键日志</br>
 * 如果合服需要合并日志数据 </br>
 * 1.先删除主键字段和主键</br>
 * ALTER TABLE `tableName` DROP COLUMN `id`, DROP PRIMARY KEY;</br>
 * 2.导出包含完整信息的insert语句</br>
 * mysqldump -hhost -Pport -uusername -p'password' --skip-extended-insert
 * --complete-insert -t dbname (more...) table（more...） >backup.sql</br>
 * 3.再把数据导入新的数据库</br>
 * source 123.sql</br>
 * 
 * @author jason</br>
 */
abstract public class LoggerEntity extends DBEntity {

	@PK(auto = true, begin = 1)
	@Persistent(name = "id", dataType = DataType.LONG, comment = "自增唯一long型主键,可以通过重写构造器强制设置auto来改变自增策略")
	private long id;

	/**
	 * 如果要使用非自增主键策略
	 * </p>
	 * 需要修改为@PK(auto = false),或者在子类构造器强制设置auto=false也可以
	 * </p>
	 * 但是在插入前需要setId(id)
	 */
	protected boolean auto;

	public LoggerEntity() {
		super();

		if (DBEntityUtils.keySet(getEntityType()).size() > 1)
			throw new RuntimeException("只允许一个long型主键:" + getEntityType());

		auto = DBEntityUtils.autoPk(getEntityType());
		setFirstCreateTime(new Date());
		atomicReferenceOptions().set(MySQLable.Options.INSERT);
	}

	@Override
	public Serializable[] getPks() {
		return new Serializable[] { id };
	}

	@Override
	public String getKey() {
		return String.valueOf(id);
	}

	/**
	 * 是否自增主键
	 * 
	 * @return
	 */
	public boolean isAuto() {
		return auto;
	}

	public long getId() {
		return id;
	}

	/**
	 * 只有当设置主键不自增时才可以调用该方法自行处理主键
	 * 
	 * @param id
	 */
	public void setId(long id) {
		if (auto)
			throw new RuntimeException("自增主键不可以设置值:" + getClass());
		if (id == 0)
			throw new RuntimeException("非增主键的值不可以为0:" + getClass());
		this.id = id;
	}

	@Override
	public void update() {
		throw new RuntimeException();
	}

	@Override
	public void delete() {
		throw new RuntimeException();
	}

	@Override
	public String mergeRoleKey() {
		return null;
	}
}
