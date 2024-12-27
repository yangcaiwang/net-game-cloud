package com.ycw.core.internal.ranklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
/**
 * <跨服排行榜接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface ICrossRankList<E extends AbstractRankMember<E>> {
    /**
     * 排行榜默认容量
     */
    int DEFAULT_SIZE = 200;

    int getTotal();

    Map<Integer, ArrayList<E>> getGroupList();

    void initMembers(int groupId, Iterable<E> members);

    void removeMember(int groupId, E member);

    void removeMemberById(int groupId, String memberId);

    int commit(int groupId, E member);

    E getMemberById(int groupId, String id);

    E getMemberByRank(int groupId, int rank);

    int getPages(int groupId, int num);

    List<E> getSubListByPredicate(int groupId, Predicate<? super E> predicate);

    List<E> getSubListByIndex(int groupId, int fromIndex, int toIndex);

    List<E> getSubListByPage(int groupId, int page, int num);

    List<E> getAllRankList(int groupId);

    public int getSize(int groupId);

    void initSortedRankList(int groupId);

    void initSortedRankList(int groupId, Iterable<E> members);

    void exChangeMember(int groupId, E member1, E member2);
}
