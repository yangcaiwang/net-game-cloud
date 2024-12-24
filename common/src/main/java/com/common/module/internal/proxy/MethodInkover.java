
package com.common.module.internal.proxy;

import java.lang.reflect.Method;

public interface MethodInkover {

	/**
	 * 方法调用前过滤
	 * 
	 * @param method
	 *            方法
	 * @param args
	 *            方法参数
	 * @return
	 */
	default boolean filterBefore(Method method, Object[] args) {
		return false;
	};

	/**
	 * 方法调用前执行
	 * 
	 * @param method
	 *            方法
	 * @param args
	 *            方法参数
	 * @return
	 */
	default void doBefore(Method method, Object[] args) {
	};

	/**
	 * 方法调用后过滤
	 * 
	 * @param method
	 *            方法
	 * @param args
	 *            方法参数
	 * @return
	 */
	default boolean filterAfter(Method method, Object[] args, Object result) {
		return false;
	};

	/**
	 * 方法调用后执行
	 * 
	 * @param method
	 *            方法
	 * @param args
	 *            方法参数
	 * @return
	 */
	default void doAfter(Method method, Object[] args, Object result) {
	}
}
