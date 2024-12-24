
package com.common.module.internal.random;

/**
 * 概率接口,默认实现了比较器（概率从大到小）
 */
public interface Probable extends Comparable<Probable> {

	/**
	 * 概率
	 * 
	 * @return
	 */
	int getProb();

	@Override
	default int compareTo(Probable o) {

		return o.getProb() - this.getProb();
	}
}
