
package com.common.module.internal.ranklist;

public interface IRankMember<E extends IRankMember<E>> extends Comparable<E> {

	String getId();

	int getRank();
}
