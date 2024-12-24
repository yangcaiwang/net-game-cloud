
package com.common.module.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.Map.Entry;
public class CollectionUtils {

	/**
	 * 只从Map中取出第一条数据
	 * 
	 * @param map
	 * @return
	 */
	static public <K, V> Entry<K, V> peekFirst(Map<K, V> map) {

		return peekFirst(map.entrySet());
	}

	/**
	 * 从迭代器取出第一条数据
	 * 
	 * @param iterable
	 * @return
	 */
	static public <T> T peekFirst(Iterable<T> iterable) {

		Iterator<T> iterator = iterable.iterator();
		if (!iterator.hasNext()) {
			return null;
		}
		return iterator.next();
	}

	static public <T> boolean isEmpty(Iterable<T> iterable) {

		return iterable == null || !iterable.iterator().hasNext();
	}

	/**
	 * @param c1
	 * @param c2
	 * @return c1中所有元素c2中不包含的
	 */
	static public <E> Set<E> setDifferences(Collection<E> c1, Collection<E> c2) {

		Set<E> set = Sets.newHashSet();
		c1.forEach(e -> {
			if (!c2.contains(e))
				set.add(e);
		});
		return set;
	}

	/**
	 * 
	 * @param oldMap
	 * @param newMap
	 * @return oldMap -- newMap 所有发生变化的元素
	 */
	static public <K> Map<K, Long> mapDifferences(Map<K, Long> oldMap, Map<K, Long> newMap) {
		Map<K, Long> m = Maps.newHashMap();
		newMap.keySet().forEach((k) -> {
			if (!oldMap.containsKey(k)) {
				m.put(k, newMap.get(k));
			} else if (newMap.get(k).compareTo(oldMap.get(k)) != 0) {
				m.put(k, newMap.get(k) - oldMap.get(k));
			}
		});
		return m;
	}

	static public <E> boolean isEmpty(Collection<E> c) {

		return c == null || c.isEmpty();
	}

	static public <K, V> boolean isEmpty(Map<K, V> map) {

		return map == null || map.isEmpty();
	}

	static public <E> boolean isEmpty(E... arr) {
		return arr == null || arr.length == 0;
	}
	static public boolean isEmpty(int[] arr) {
		return arr == null || arr.length == 0;
	}
	static public boolean isEmpty(int[][] arr) {
		return ArraysUtils.isEmpty(arr);
	}
	static public <E> List<E> subList(List<E> src, int begin, int end) {

		try {
			return Lists.newArrayList(src.subList(begin, end));
		} catch (Exception e) {
			throw new RuntimeException(String.format("subList from[%d]to[%d],size=[%d]", begin, end, src.size()), e);
		}
	}

	static public <E> Set<E> unmodifiableSet(Set<E> s) {

		return Collections.unmodifiableSet(s);
	}

	static public <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> s) {

		return Collections.unmodifiableSortedSet(s);
	}

	static public <E> NavigableSet<E> unmodifiableNavigableSet(NavigableSet<E> s) {

		return Collections.unmodifiableNavigableSet(s);
	}

	static public <E> List<E> unmodifiableList(List<E> src) {

		return Collections.unmodifiableList(src);
	}

	static public <E> Collection<E> unmodifiableCollection(Collection<E> c) {

		return Collections.unmodifiableCollection(c);
	}

	static public <K, V> Map<K, V> unmodifiableMap(Map<K, V> m) {

		return Collections.unmodifiableMap(m);
	}

	static public <K, V> SortedMap<K, V> unmodifiableSortedMap(SortedMap<K, V> m) {

		return Collections.unmodifiableSortedMap(m);
	}

	static public <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, V> m) {

		return Collections.unmodifiableNavigableMap(m);
	}
}
