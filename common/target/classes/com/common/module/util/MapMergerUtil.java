
package com.common.module.util;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.Map.Entry;

public class MapMergerUtil {

	/**
	 * 合并两个map,key相同,value相加,如果是减少的,value请转成负数
	 *
	 * @param master
	 *            原始数据
	 * @param branch
	 *            将要合并到原始数据的临时数据
	 * @return 修改后的属性
	 */
	public static <K> Map<K, Long> merge(Map<K, Long> master, Map<K, Long> branch) {

		for (Entry<K, Long> entry : branch.entrySet()) {
			merge(master, entry.getKey(), entry.getValue());
		}
		Map<K, Long> result = Maps.newHashMap();
		for (K k : branch.keySet()) {
			result.put(k, master.get(k));
		}
		return result;
	}

	/**
	 * 合并数据
	 *
	 * @param master
	 *            原始数据
	 * @param key
	 *            新增数据的key
	 * @param value
	 *            新增数据的value,如果需要减少就传入负数
	 * @return 修改后的值
	 */
	public static <K> Long merge(Map<K, Long> master, K key, long value) {
		return merge(master, key, value, true);
	}

	/**
	 * 合并数据
	 *
	 * @param master
	 *            原始数据
	 * @param key
	 *            新增数据的key
	 * @param value
	 *            新增数据的value,如果需要减少就传入负数
	 * @param negative
	 *            是否允许结果为负数
	 * @return 修改后的值
	 */
	public static <K> Long merge(Map<K, Long> master, K key, long value, boolean negative) {
		master.merge(key, value, (v1, v2) -> v1 + v2);
		if (!negative && master.get(key) < 0L) {
			master.put(key, 0L);
		}
		return master.get(key);
	}

	/**
	 * 得到变化后的新属性集合，主要用于战斗属性计算oldMap:{<101,500>,<102,600>,<103,900>},newMap:{
	 * <102,800>,<103,900>,<104,600>}, 属性计算后，需告诉前端变化属性为result:{<101,0>,
	 * <102,800>,<104,600>}
	 *
	 * @param oldMap
	 * @param newMap
	 * @return
	 */
	public static <K> Map<K, Long> diffMap(Map<K, Long> oldMap, Map<K, Long> newMap) {

		Map<K, Long> result = Maps.newHashMap();
		for (Entry<K, Long> entry : oldMap.entrySet()) {
			Long newValue = newMap.get(entry.getKey());
			if (newValue == null) {
				result.put(entry.getKey(), 0L);
			} else if (!entry.getValue().equals(newValue)) {
				result.put(entry.getKey(), newValue);
			}
		}
		for (Entry<K, Long> entry : newMap.entrySet()) {
			if (!oldMap.containsKey(entry.getKey())) {
				result.put(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	/**
	 * 删除oldMap 合并newMap
	 *
	 * @param oldMap
	 * @param newMap
	 * @param <K>
	 * @return
	 */
	public static <K> void changeMap(Map<K, Long> sourceMap, Map<K, Long> oldMap, Map<K, Long> newMap) {
		Map<K, Long> _oldMap = Maps.newHashMap();
		for (K k : oldMap.keySet()) {
			_oldMap.put(k, -oldMap.get(k));
		}
		merge(sourceMap, _oldMap);
		merge(sourceMap, newMap);
	}

	public static void main(String[] args) {

		Map<Integer, Long> sourceMap = Maps.newHashMap();
		sourceMap.put(101, 100L);
		Map<Integer, Long> oldMap = Maps.newHashMap();
		oldMap.put(101, 50L);
		Map<Integer, Long> newMap = Maps.newHashMap();
		newMap.put(101, 150L);
		changeMap(sourceMap, oldMap, newMap);
		System.out.println(StringUtils.toString(sourceMap));
	}
}
