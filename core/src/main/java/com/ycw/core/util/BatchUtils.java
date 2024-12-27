
package com.ycw.core.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <批量操作工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class BatchUtils {

	private static final Logger log = LoggerFactory.getLogger(BatchUtils.class);

	/**
	 * 分批
	 * 
	 * @param c
	 *            源数据
	 * @param num
	 *            每一批数量
	 * @return LinkedHashMap {batchIndex:list},batchIndex 从1开始
	 */
	public static <E> Map<Integer, List<E>> toMap(Iterable<E> c, int num) {
		long begin = System.currentTimeMillis();
		int total = 0;
		int batches = 0;
		try {
			if (CollectionUtils.isEmpty(c)) {
				return Collections.emptyMap();
			}
			Map<Integer, List<E>> result = Maps.newLinkedHashMap();
			Iterator<E> iterator = c.iterator();
			int batchIndex = 1;
			while (iterator.hasNext()) {
				List<E> list = result.computeIfAbsent(batchIndex, v -> Lists.newArrayList());
				E element = iterator.next();
				list.add(element);
				if (list.size() == num) {
					++batchIndex;
				}
			}
			return result;
		} catch (Exception e) {
			log.error(e.getMessage());
			return Collections.emptyMap();
		} finally {
			long end = System.currentTimeMillis();
			log.debug("mapBatch used times =" + (end - begin) + " ms,total = " + total + ",oneBatchNum =" + num + ", batchs=" + batches);
		}
	}

	/**
	 * 分批
	 * 
	 * @param c
	 *            源数据
	 * @param num
	 *            每一批数量
	 * @return List[List]
	 */
	public static <E> List<List<E>> toLists(Iterable<E> c, int num) {
		long begin = System.currentTimeMillis();
		int total = 0;
		int batches = 0;
		try {
			if (CollectionUtils.isEmpty(c)) {
				return Collections.emptyList();
			}
			List<List<E>> result = Lists.newArrayList();
			Iterator<E> iterator = c.iterator();
			int batchIndex = 0;
			while (iterator.hasNext()) {
				List<E> list = result.size() <= batchIndex ? null : result.get(batchIndex);
				if (list == null) {
					list = Lists.newArrayList();
					result.add(list);
				}
				E element = iterator.next();
				list.add(element);
				if (list.size() == num) {
					++batchIndex;
				}
			}
			return result;
		} catch (Exception e) {
			return Collections.emptyList();
		} finally {
			long end = System.currentTimeMillis();
			log.debug("listBatch used times =" + (end - begin) + " ms,total = " + total + ",oneBatchNum =" + num + ", batchs=" + batches);
		}
	}

	public static void main(String[] args) {
		List<Integer> c = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9);

		List<List<Integer>> lists = toLists(c, 4);
		System.err.println(StringUtils.toString(lists));

		Map<Integer, List<Integer>> maps = toMap(c, 4);
		System.err.println(StringUtils.toString(maps));
	}

}
