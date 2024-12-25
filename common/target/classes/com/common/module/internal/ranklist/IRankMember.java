
package com.common.module.internal.ranklist;

/**
 * <排行榜成员接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface IRankMember<E extends IRankMember<E>> extends Comparable<E> {

	String getId();

	int getRank();
}
