
package com.common.module.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <数组工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ArraysUtils {

	public static <E> boolean contains(E[] arr, E target) {

		for (int i = arr.length; --i >= 0;) {
			if (arr[i].equals(target)) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(int[] arr, int target) {
		if (arr == null || arr.length == 0) {
			return false;
		}

		for (int i = arr.length; --i >= 0;) {
			if (arr[i] == target) {
				return true;
			}
		}
		return false;
	}

	public static boolean equals(byte[] arr1, byte[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(short[] arr1, short[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(int[] arr1, int[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(long[] arr1, long[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(float[] arr1, float[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(double[] arr1, double[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(boolean[] arr1, boolean[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(char[] arr1, char[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(String[] arr1, String[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean equals(Object[] arr1, Object[] arr2) {

		return Arrays.equals(arr1, arr2);
	}

	public static boolean deepEquals(Object[] arr1, Object[] arr2) {

		return Arrays.deepEquals(arr1, arr2);
	}

	public static int hashCode(Object[] arr) {

		return Arrays.hashCode(arr);
	}

	public static int deepHashCode(Object[] arr) {

		return Arrays.deepHashCode(arr);
	}

	public static <E> E[] subArray(E[] src, int begin, int end) {

		try {
			return Arrays.copyOfRange(src, begin, end);
		} catch (Exception e) {
			throw new RuntimeException(String.format("subArray from[%d]to[%d],size=[%d]", begin, end, src.length), e);
		}
	}

	final static public <E> List<E> toList(E... args) {

		return new ArrayList<E>(Arrays.asList(args));
	}

	final static public <E> E[] toArray(Collection<E> c, Class<E> cls) {

		E[] arr = (E[]) Array.newInstance(cls, c.size());
		Iterator<E> iterator = c.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			arr[i++] = iterator.next();
		}
		return arr;
	}

	public static int[] addArrayElement(int[] arr, int value) {
		if (arr == null) {
			arr = new int[]{value};
			return arr;
		}
		boolean find = false;
		for (int ar : arr) {
			if (ar == value) {
				find = true;
				break;
			}
		}
		if (!find) {
			int len = arr.length;
			int[] arrNew = new int[len + 1];
			System.arraycopy(arr, 0, arrNew, 0, arr.length);
			arrNew[len] = value;
			return arrNew;
		}
		return arr;
	}

	public static int[] deleteArrayElement(int[] arr, int... values) {
		if (arr == null || arr.length == 0) {
			return arr;
		}
		Set<Integer> collect = Arrays.stream(arr).boxed().collect(Collectors.toSet());
		collect.removeIf(v -> contains(values, v));
		return toArray(new ArrayList<>(collect));
	}
	/**
	 * 二分法倒转,效率更高
	 * 
	 * @param array
	 * @return
	 */
	public static byte[] reversed2(byte[] array) {

		int start = 0;
		int end = array.length - 1;
		int mid = array.length / 2;
		while (true) {
			if (start >= mid && end <= mid)
				break;
			byte s = array[start];
			byte e = array[end];
			array[end] = s;
			array[start] = e;
			start++;
			end--;
		}
		return array;
	}

	/**
	 * 二分法倒转,效率更高
	 * 
	 * @param array
	 * @return
	 */
	public static char[] reversed2(char[] chars) {
		int start = 0;
		int end = chars.length - 1;
		int mid = chars.length / 2;
		while (true) {
			if (start >= mid && end <= mid)
				break;
			char s = chars[start];
			char e = chars[end];
			chars[end] = s;
			chars[start] = e;
			start++;
			end--;
		}
		return chars;
	}

	/**
	 * 二分法倒转,效率更高
	 * 
	 * @param array
	 * @return
	 */
	public static <E> E[] reversed2(E[] array) {
		int start = 0;
		int end = array.length - 1;
		int mid = array.length / 2;
		while (true) {
			if (start >= mid && end <= mid)
				break;
			E s = array[start];
			E e = array[end];
			array[end] = s;
			array[start] = e;
			start++;
			end--;
		}
		return array;
	}

	/**
	 * 二分法倒转,效率更高
	 * 
	 * @param list
	 * @return
	 */
	public static <E> List<E> reversed2(List<E> list) {
		int start = 0;
		int end = list.size() - 1;
		int mid = list.size() / 2;
		while (true) {
			if (start >= mid && end <= mid)
				break;
			E s = list.get(start);
			E e = list.get(end);
			list.set(end, s);
			list.set(start, e);
			start++;
			end--;
		}
		return list;
	}

	/**
	 * 倒转
	 * 
	 * @return
	 */
	final static public <E> List<E> reversedList(List<E> list) {

		List<E> result = new ArrayList<>(list.size());
		for (int i = list.size(); --i >= 0;) {
			result.add(list.get(i));
		}
		return result;
	}

	/**
	 * 倒转
	 * 
	 * @param c
	 * @return
	 */
	final static public <E> E[] reversedArray(E[] array, Class<E> cls) {

		E[] result = (E[]) Array.newInstance(cls, array.length);
		for (int i = array.length; --i >= 0;) {
			result[i] = array[array.length - 1 - i];
		}
		return result;
	}

	/**
	 * 倒转
	 * 
	 * @param array
	 * @return
	 */
	final static public byte[] reversedByteArray(byte[] array) {

		byte[] result = new byte[array.length];
		for (int i = array.length; --i >= 0;) {
			result[i] = array[array.length - 1 - i];
		}
		return result;
	}

	public static <E> E find2One(List<E> list, java.util.function.Predicate<E> predicate) {
		return list.stream().filter(e -> predicate.test(e)).findFirst().orElse(null);
	}

	public static <E> List<E> find2(List<E> list, java.util.function.Predicate<E> predicate) {
		return list.stream().filter(e -> predicate.test(e)).collect(Collectors.toList());
	}

	final static public int[] toArray(Integer[] arr) {

		int[] _arr = new int[arr.length];
		for (int i = 0; i < _arr.length; i++) {
			_arr[i] = arr[i];
		}
		return _arr;
	}

	final static public int[] toArray(List<Integer> list) {

		int[] _arr = new int[list.size()];
		for (int i = 0; i < _arr.length; i++) {
			_arr[i] = list.get(i);
		}
		return _arr;
	}

	final static public boolean isShun(List<Integer> list) {

		Collections.sort(list);
		for (int i = 0; i < list.size() - 1; i++) {
			int now = list.get(i);
			int next = list.get(i + 1);
			if (next - now != 1) {
				return false;
			}
		}
		return true;
	}

	public static boolean isShun(int[] arr) {

		Arrays.sort(arr);
		for (int i = 0; i < arr.length - 1; i++) {
			int now = arr[i];
			int next = arr[i + 1];
			if (next - now != 1) {
				return false;
			}
		}
		return true;
	}

	public static <E> boolean isEmpty(E[] arr) {

		return arr == null || arr.length < 1;
	}

	public static <E> boolean isEmpty(List<E> list) {

		return list == null || list.isEmpty();
	}

	/**
	 * 判断二维数组里面是否有元素
	 * 
	 * @param arr
	 * @return
	 */
	public static boolean isEmpty(int[][] arr) {

		if (arr == null)
			return true;
		for (int[] es : arr) {
			if (es != null && es.length > 0)
				return false;
		}
		return true;
	}

	public static int[][] list2Arr(List<int[]> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}

		int[][] arrs = new int[list.size()][];
		int size = list.size();
		for (int i = 0; i < size; i++) {
			int[] arr = list.get(i);
			arrs[i] = new int[arr.length];
			System.arraycopy(arr, 0, arrs[i], 0, arr.length);
		}
		return arrs;
	}

	public static int[] subArray(int[] arr, int begin, int len) {
		if (arr == null || len <= 0 || begin < 0|| arr.length <= begin || arr.length < begin + len) {
			return null;
		}

		int[] result = new int[len];
		for (int i = begin, index = 0; index < len; i++, index++) {
			result[index] = arr[i];
		}
		return result;
	}

	public static int[][] addArray(int[][] arr, int key, int value) {
		if (arr == null) {
			arr = new int[][]{{key, value}};
			return arr;
		}
		boolean find = false;
		for (int[] ar : arr) {
			if (ar[0] == key) {
				ar[1] = value;
				find = true;
				break;
			}
		}
		if (!find) {
			int len = arr.length;
			int[][] arrNew = new int[len + 1][];
			System.arraycopy(arr, 0, arrNew, 0, arr.length);
			arrNew[len] = new int[]{key, value};
			return arrNew;
		}
		return arr;
	}

	public static int[][] addArrayValue(int[][] arr, int key, int value) {
		if (arr == null) {
			arr = new int[][]{{key, value}};
			return arr;
		}
		boolean find = false;
		for (int[] ar : arr) {
			if (ar[0] == key) {
				ar[1] += value;
				find = true;
				break;
			}
		}
		if (!find) {
			int len = arr.length;
			int[][] arrNew = new int[len + 1][];
			System.arraycopy(arr, 0, arrNew, 0, arr.length);
			arrNew[len] = new int[]{key, value};
			return arrNew;
		}
		return arr;
	}

	public static int[][] removeArrayKey(int[][] arr, int... keys) {
		Map<Integer, Integer> integerIntegerMap = arrayToMap(arr);
		boolean find = false;
		for (int key : keys) {
			Integer remove = integerIntegerMap.remove(key);
			if (remove != null) {
				find = true;
			}
		}

		if (!find) {
			return arr;
		}

		return mapToArray(integerIntegerMap);
	}

	public static int getArrayValue(int[][] arr, int key, int defaultValue) {
		if (arr == null) {
			return defaultValue;
		}
		for (int[] ar : arr) {
			if (ar[0] == key) {
				return ar[1];
			}
		}
		return defaultValue;
	}

	public static int[][] mapToArray(Map<Integer, Integer> map) {
		int[][] array = new int[map.size()][2];
		int index = 0;
		for (int key : map.keySet()) {
			Integer value = map.get(key);
			if (value == null) {
				continue;
			}
			array[index][0] = key;
			array[index][1] = value;
			index++;
		}
		return array;
	}

	public static Map<Integer, Integer> arrayToMap(int[][] array) {
		return arrayToMap(array, false);
	}

	/**
	 * array 转map
	 * @param array
	 * @param repeatedKey 是否允许key重复 false - 覆盖key对应的值
	 * @return
	 */
	public static Map<Integer, Integer> arrayToMap(int[][] array, boolean repeatedKey) {
		if (array == null) {
			return Collections.emptyMap();
		}
		Map<Integer, Integer> map = new HashMap<>();
		for (int[] ar : array) {
			if (ar.length < 2) {
				continue;
			}
			if (repeatedKey) {
				map.put(ar[0], map.getOrDefault(ar[0], 0) + ar[1]);
			} else {
				map.put(ar[0], ar[1]);
			}
		}
		return map;
	}
	public static void main(String[] args) {
		List<Integer> list = Lists.newArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		System.err.println(list);

		List<Integer> list2 = reversed2(list);
		System.err.println(list2);

		int[] index = new int[]{1,2,3,4,5,6,7};
		int[] ints = ArrayUtils.subarray(index, 8, index.length);
		for (int i : ints) {
			System.err.println(i);
		}

		int[][] arr = new int[][]{{0,1}};
		arr = addArray(arr, 2, 1);

		for (int i = 0; i < arr.length; i++) {
			System.err.println(arr[i][0] + ";" + arr[i][1]);
		}
		arr = removeArrayKey(arr, 2);
		arr = removeArrayKey(arr, 0);
		System.err.println(arr.length);
		for (int i = 0; i < arr.length; i++) {
			System.err.println(arr[i][0] + ";" + arr[i][1]);
		}
	}
}
