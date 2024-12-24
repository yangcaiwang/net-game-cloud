
package com.common.module.internal.db.entity;

/**
 * 虚拟机内存对象
 */
public interface JVMable {

	/** 以键值对保存的对象数据在jvm中的key */
	String getKey();
}
