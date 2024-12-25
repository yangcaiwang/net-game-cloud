
package com.common.module.internal.ranklist;

import java.util.List;
import java.util.function.Predicate;

/**
 * <排行榜接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
interface IRankList<E extends AbstractRankMember<E>> {

	/**
	 * 排行榜默认容量
	 */
	int DEFAULT_SIZE = 200;

	void initMembers(Iterable<E> members);

	void removeMember(E member);

	void removeMemberById(String memberId);

	int commit(E member);

	E getMemberById(String id);

	E getMemberByRank(int rank);

	int getPages(int num);

	List<E> getSubListByPredicate(Predicate<? super E> predicate);

	List<E> getSubListByIndex(int fromIndex, int toIndex);

	List<E> getSubListByPage(int page, int num);

	List<E> getAllRankList();

	public int getSize();

	public int getTotal();

	void initSortedRankList();

	void initSortedRankList(Iterable<E> members);

	void exChangeMember(E member1, E member2);
}
