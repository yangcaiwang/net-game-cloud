
package com.common.module.util;

final public class HashCodeUtil {

	/**
	 * 任意对象hashCode的绝对值(+值)
	 * 
	 * @param obj 对象
	 * @return int
	 */
	public static int absHashCode(Object obj) {
		int hashCode = obj.hashCode();
		if (hashCode == Integer.MIN_VALUE) {
			hashCode += Integer.MAX_VALUE;
		}
		if (hashCode < 0) {
			hashCode = -hashCode;
		}
		return hashCode;
	}

	public static void main(String[] args) {
		System.err.println(absHashCode(-10));
		System.err.println(absHashCode(Long.MAX_VALUE));
		System.err.println(absHashCode(Long.MIN_VALUE));
	}
}