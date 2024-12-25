
package com.common.module.internal.ranklist;

import com.google.common.collect.Lists;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * <排行榜算法工具类>
 * <p>
 * ps: 快速算法
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class SortUtils {

    public static void main(String[] args) {
        List<Integer> integers = Lists.newArrayList(1);
        quickSort(integers, 0, integers.size() - 1, (i1, i2) -> {
            return i1 - i2;
        });
    }

    public static void quickSort(int[] array, int low, int high) {
        if (array == null || array.length < 2)
            return;
        int start = low;
        int end = high;
        int key = array[low];
        while (end > start) {
            // 从后往前比较
            while (end > start && array[end] >= key) // 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
                end--;
            if (array[end] <= key) {
                int temp = array[end];
                array[end] = array[start];
                array[start] = temp;
            }
            // 从前往后比较
            while (end > start && array[start] <= key)// 如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
                start++;
            if (array[start] >= key) {
                int temp = array[start];
                array[start] = array[end];
                array[end] = temp;
            }
            // 此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        // 递归
        if (start > low)
            quickSort(array, low, start - 1);// 左边序列。第一个索引位置到关键值索引-1
        if (end < high)
            quickSort(array, end + 1, high);// 右边序列。从关键值索引+1到最后一个
    }

    public static <E extends Comparable<E>> void quickSort(E[] array, int low, int high) {
        if (array == null || array.length < 2)
            return;
        int start = low;
        int end = high;
        E key = array[low];
        while (end > start) {
            // 从后往前比较
            while (end > start && array[end].compareTo(key) >= 0) // 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
                end--;
            if (array[end].compareTo(key) <= 0) {
                E temp = array[end];
                array[end] = array[start];
                array[start] = temp;
            }
            // 从前往后比较
            while (end > start && array[start].compareTo(key) <= 0)// 如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
                start++;
            if (array[start].compareTo(key) >= 0) {
                E temp = array[start];
                array[start] = array[end];
                array[end] = temp;
            }
            // 此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        // 递归
        if (start > low)
            quickSort(array, low, start - 1);// 左边序列。第一个索引位置到关键值索引-1
        if (end < high)
            quickSort(array, end + 1, high);// 右边序列。从关键值索引+1到最后一个
    }

    public static <E> void quickSort(E[] array, int low, int high, Comparator<E> comparator) {
        if (array == null || array.length < 2)
            return;
        int start = low;
        int end = high;
        E key = array[low];
        while (end > start) {
            // 从后往前比较
            while (end > start && comparator.compare(array[end], key) >= 0) // 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
                end--;
            if (comparator.compare(array[end], key) <= 0) {
                E temp = array[end];
                array[end] = array[start];
                array[start] = temp;
            }
            // 从前往后比较
            while (end > start && comparator.compare(array[start], key) <= 0)// 如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
                start++;
            if (comparator.compare(array[start], key) >= 0) {
                E temp = array[start];
                array[start] = array[end];
                array[end] = temp;
            }
            // 此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        // 递归
        if (start > low)
            quickSort(array, low, start - 1, comparator);// 左边序列。第一个索引位置到关键值索引-1
        if (end < high)
            quickSort(array, end + 1, high, comparator);// 右边序列。从关键值索引+1到最后一个
    }

    public static <E extends Comparable<E>> void quickSort(List<E> list, int low, int high) {
        if (list == null || list.size() < 2)
            return;
        if (!(list instanceof ArrayList))
            LoggerFactory.getLogger(SortUtils.class).warn("!(list instanceof ArrayList)list=" + list.getClass());
        int start = low;
        int end = high;
        E key = list.get(low);
        while (end > start) {
            // 从后往前比较
            while (end > start && list.get(end).compareTo(key) >= 0) // 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
                end--;
            if (list.get(end).compareTo(key) <= 0) {
                E temp = list.get(end);
                list.set(end, list.get(start));
                list.set(start, temp);
            }
            // 从前往后比较
            while (end > start && list.get(start).compareTo(key) <= 0)// 如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
                start++;
            if (list.get(start).compareTo(key) >= 0) {
                E temp = list.get(start);
                list.set(start, list.get(end));
                list.set(end, temp);
            }
            // 此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        // 递归
        if (start > low)
            quickSort(list, low, start - 1);// 左边序列。第一个索引位置到关键值索引-1
        if (end < high)
            quickSort(list, end + 1, high);// 右边序列。从关键值索引+1到最后一个
    }

    public static <E> void quickSort(List<E> list, int low, int high, Comparator<E> comparator) {
        if (list == null || list.size() < 2)
            return;
        if (!(list instanceof ArrayList))
            LoggerFactory.getLogger(SortUtils.class).warn("!(list instanceof ArrayList)list=" + list.getClass());
        int start = low;
        int end = high;
        E key = list.get(low);
        while (end > start) {
            // 从后往前比较
            while (end > start && comparator.compare(list.get(end), key) >= 0) // 如果没有比关键值小的，比较下一个，直到有比关键值小的交换位置，然后又从前往后比较
                end--;
            if (comparator.compare(list.get(end), key) <= 0) {
                E temp = list.get(end);
                list.set(end, list.get(start));
                list.set(start, temp);
            }
            // 从前往后比较
            while (end > start && comparator.compare(list.get(start), key) <= 0)// 如果没有比关键值大的，比较下一个，直到有比关键值大的交换位置
                start++;
            if (comparator.compare(list.get(start), key) >= 0) {
                E temp = list.get(start);
                list.set(start, list.get(end));
                list.set(end, temp);
            }
            // 此时第一次循环比较结束，关键值的位置已经确定了。左边的值都比关键值小，右边的值都比关键值大，但是两边的顺序还有可能是不一样的，进行下面的递归调用
        }
        // 递归
        if (start > low)
            quickSort(list, low, start - 1, comparator);// 左边序列。第一个索引位置到关键值索引-1
        if (end < high)
            quickSort(list, end + 1, high, comparator);// 右边序列。从关键值索引+1到最后一个
    }
}
