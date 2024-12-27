
package com.ycw.gm.common.utils;


/**
 * 计算hash值
 */
final public class HashedFactory {

	/**
	 * 任意对象hashCode的绝对值(+值)
	 * 
	 * @param obj
	 * @return
	 */
	public static int absHashCode(Object obj) {
		int hashCode = obj.hashCode();
		if (hashCode == Integer.MIN_VALUE) {
			hashCode += Integer.MAX_VALUE;
		}
		if (hashCode < 0) {
			hashCode = -hashCode;
		}
//		Validate.isTrue(hashCode > 0, "%s:%s.hashCode()[%d]<0", obj.getClass(), obj, hashCode);
		return hashCode;
	}

	public static void main(String[] args) {
		System.err.println(absHashCode(-10));
		System.err.println(absHashCode(Long.MAX_VALUE));
		System.err.println(absHashCode(Long.MIN_VALUE));
	}
}
