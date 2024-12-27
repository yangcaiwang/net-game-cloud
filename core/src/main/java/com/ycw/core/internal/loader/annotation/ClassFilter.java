
package com.ycw.core.internal.loader.annotation;

/**
 * class类过滤器
 */
public interface ClassFilter {

	/**
	 * 是否认可这个类
	 */
	boolean accept(Class<?> clz);
}
