
package com.ycw.core.internal.db.entity;

/**
 * <虚拟机内存对象接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface JVMable {

	/** 以键值对保存的对象数据在jvm中的key */
	String getKey();
}
