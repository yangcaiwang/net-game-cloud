
package com.common.module.internal.ranklist;

import java.util.List;
import java.util.function.Predicate;

public interface IRankLists {

	/**
	 * 填充成员列表,这个方法只能在初始的时候调用
	 * 
	 * @param name
	 *            排行榜名称
	 * @param members
	 *            成员列表,填充进去会立刻被快速排序
	 */
	<E extends AbstractRankMember<E>> void initMembers(String name, Iterable<E> members);

	/**
	 * 创建排行榜
	 * 
	 * @param name
	 *            排行榜名称
	 * @param total
	 *            最大容量
	 * @return
	 */
	void createRankList(String name, int total);

	/**
	 * 重置排行榜
	 * 
	 * @param name
	 *            排行榜名称
	 * @return
	 */
	void resetRankList(String name);

	/**
	 * 提交成绩
	 * 
	 * @param name
	 *            排行榜名称
	 * @param member
	 *            成员,应该优先通过getMemberById或者getMemberByRank获取,没有才创建
	 */
	<E extends AbstractRankMember<E>> void commit(String name, E member);

	<E extends AbstractRankMember<E>> void commitIfNotExist(String name, int total, E member);

	/**
	 * 删除成员,慎用,排行榜通常不需要删除成员
	 * 
	 * @param name
	 * @param member
	 */
	<E extends AbstractRankMember<E>> void removeMember(String name, E member);

	/**
	 * 删除成员,慎用,排行榜通常不需要删除成员
	 * 
	 * @param name
	 * @param memberId
	 */
	void removeMemberById(String name, String memberId);

	/**
	 * 获取最大容量
	 * 
	 * @param name
	 *            排行榜名称
	 * @return
	 */
	int getTotal(String name);

	/**
	 * 获取排行榜当前成员条数
	 * 
	 * @param name
	 *            排行榜名称
	 * @return
	 */
	int getSize(String name);

	/**
	 * 获取排行榜可以分为多少页
	 * 
	 * @param name
	 *            排行榜名称
	 * @param onePageNum
	 *            每一页显示数量
	 * @return
	 */
	int getPages(String name, int onePageNum);

	/**
	 * 根据函数式条件获取成员列表
	 * 
	 * @param name
	 *            排行榜名称
	 * @param predicate
	 *            函数式条件
	 * @return
	 */
	<E extends AbstractRankMember<E>> List<E> getSubListByPredicate(String name, Predicate<? super E> predicate);

	/**
	 * 获取一个区间的排行榜成员列表,类似list.subList函数功能begin不可以为负数,end可以==size
	 * 
	 * @param name
	 *            排行榜名称
	 * @param begin
	 * @param end
	 * @return
	 */
	<E extends AbstractRankMember<E>> List<E> getSubListByIndex(String name, int begin, int end);

	/**
	 * 获取某一页成员列表
	 * 
	 * @param name
	 *            排行榜名称
	 * @param pageIndex
	 *            页码
	 * @param onePageNum
	 *            每一页显示数量
	 * @return
	 */
	<E extends AbstractRankMember<E>> List<E> getSubListByPage(String name, int pageIndex, int onePageNum);

	/**
	 * 获取所有排行榜数据
	 * @param name
	 * @param <E>
	 * @return
	 */
	<E extends AbstractRankMember<E>> List<E> getAllRankList(String name);

	/**
	 * 根据成员ID获取成员
	 * 
	 * @param name
	 *            排行榜名称
	 * @param id
	 *            成员唯一ID
	 * @return
	 */
	<E extends AbstractRankMember<E>> E getMemberById(String name, String id);

	/**
	 * 根据排名获取成员
	 * 
	 * @param name
	 *            排行榜名称
	 * @param rank
	 *            名次
	 * @return
	 */
	<E extends AbstractRankMember<E>> E getMemberByRank(String name, int rank);

	/**
	 * 检查目标排行榜是否已存在
	 * 
	 * @param name
	 *            排行榜名称
	 * @return
	 */
	boolean checkRankingIsExist(String name);

	void cleanAllRankByName(String name);

	/**
	 * 将排行榜列表内容按排名排序
	 * @param name
	 */
	<E extends AbstractRankMember<E>> void initSortedRankList(String name, Iterable<E> members);

	/**
	 * 将排行榜列表内容按排名排序
	 * @param name
	 */
	<E extends AbstractRankMember<E>> void initSortedRankList(String name);

	/**
	 * 相互换位置
	 * @param member1
	 * @param member2
	 * @param <E>
	 */
	<E extends AbstractRankMember<E>> void exChangeMember(String name, E member1, E member2);
}
