
package com.common.module.internal.random;

import com.common.module.util.ArraysUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;

import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <概率随机工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ProbabilityUtils {

    public static <T extends Probable> int getSum(List<T> list) {

        return list.stream().mapToInt(T::getProb).sum();// 和
    }

    public static <T extends Probable> int getMax(List<T> list) {

        return list.stream().mapToInt(T::getProb).max().getAsInt();// 最大
    }

    public static <T extends Probable> int getMin(List<T> list) {

        return list.stream().mapToInt(T::getProb).min().getAsInt();// 最小
    }

    public static <T extends Probable> double getAverage(List<T> list) {

        return list.stream().mapToDouble(T::getProb).average().getAsDouble();// 平均值
    }

    /**
     * 并发安全普通随机数对象
     */
    public static Random random() {

        return ThreadLocalRandom.current();
    }

    /**
     * 加密随机数对象
     */
    public static SecureRandom secureRandom() {

        return new SecureRandom();
    }

    /**
     * 随机从min~max
     */
    public static int random(int min, int max) {

        return random().nextInt(max - min + 1) + min;
    }

    public static boolean nextBoolean() {

        return RandomUtils.nextBoolean();
    }

    public static byte[] nextBytes(int count) {

        return RandomUtils.nextBytes(count);
    }

    public static double nextDouble() {

        return RandomUtils.nextDouble();
    }

    /**
     * 随机 startInclusive ~ endInclusive
     */
    public static double nextDouble(double startInclusive, double endInclusive) {

        return RandomUtils.nextDouble(startInclusive, endInclusive);
    }

    public static float nextFloat() {

        return RandomUtils.nextFloat();
    }

    /**
     * 随机 startInclusive ~ endInclusive
     */
    public static float nextFloat(float startInclusive, float endInclusive) {

        return RandomUtils.nextFloat(startInclusive, endInclusive);
    }

    public static int nextInt() {

        return RandomUtils.nextInt();
    }

    /**
     * 随机 startInclusive ~ endExclusive-1
     */
    public static int nextInt(int startInclusive, int endExclusive) {

        return RandomUtils.nextInt(startInclusive, endExclusive);
    }

    public static long nextLong() {

        return RandomUtils.nextLong();
    }

    /**
     * 随机 startInclusive ~ endExclusive-1
     */
    public static long nextLong(long startInclusive, long endExclusive) {

        return RandomUtils.nextLong(startInclusive, endExclusive);
    }

    /**
     * 随机0~(n-1)
     */
    public static int random(int n) {

        if (n <= 0) {
            throw new RuntimeException("random:" + n);
        }
        return random().nextInt(n);
    }

    /**
     * 按概率从列表中选择一个元素
     */
    public static <T extends Probable> T chooseByProb(Iterable<T> iterable) {
        List<T> list = Lists.newArrayList(iterable);
        int sum = getSum(list);
        if (sum == 0) return null;
        return chooseByProb(sum, list);
    }

    /**
     * 通过反射获取权重方法，找到一个权重值 (权重方法必须是int型)
     */
    public static <T> T chooseByProb(List<T> list, String getMethod) {
        int sum = 0;
        try {
            for (T t : list) {
                Method declaredMethod = t.getClass().getMethod(getMethod);
                Class<?> returnType = declaredMethod.getReturnType();
                if (returnType == int.class || returnType == Integer.class) {
                    int invoke = (int) declaredMethod.invoke(t);
                    sum += invoke;
                }
            }
            if (sum == 0) return null;
            int r = random(1, sum);
            Collections.shuffle(list);
            int temp = 0;
            for (T t : list) {
                Method declaredMethod = t.getClass().getMethod(getMethod);
                int invoke = (int) declaredMethod.invoke(t);
                temp += invoke;
                if (r <= temp) {
                    return t;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 按概率从列表中选择一个元素
     */
    public static <T extends Probable> T chooseByProb(int total, Iterable<T> iterable) {
        int r = random(1, total);
        List<T> tempList = Lists.newArrayList(iterable);
        int s = 0;
        for (T p : tempList) {
            s += p.getProb();
            if (r <= s) {
                return p;
            }
        }
        return null;
    }

    /**
     * 纯随机，从列表中随机num个元素,不重复
     */
    public static <T> List<T> chooseByNum(Iterable<T> iterable, int num) {
        List<T> result = new ArrayList<>(num);
        List<T> tempList = Lists.newArrayList(iterable);
        while (tempList.size() > 0 && result.size() < num) {
            result.add(tempList.remove(random(tempList.size())));
        }
        return result;
    }


    /**
     * 从列表中按权重随机 num 个元素,可重复
     */
    public static <T extends Probable> List<T> chooseByNumAndWeight(Iterable<T> iterable, int num) {
        return chooseByNumAndWeight(iterable, num, true);
    }

    /**
     * 从列表中按权重随机 num 个元素,不重复
     */
    public static <T extends Probable> List<T> chooseByNumAndWeight(Iterable<T> iterable, int num, boolean repeat) {
        List<T> result = new ArrayList<>(num);
        List<T> tempList = Lists.newArrayList(iterable);
        for (int i = 0; i < num; i++) {
            T t = chooseByProb(tempList);
            if (t != null) {
                result.add(t);
                if (!repeat) {
                    tempList.remove(t);
                }
            }
        }
        return result;
    }

    /**
     * 纯随机，从列表中随机count次，可重复
     */
    public static <T> List<T> chooseByCount(Iterable<T> iterable, int count) {
        List<T> result = new ArrayList<>(count);
        ArrayList<T> tempList = Lists.newArrayList(iterable);
        AtomicInteger c = new AtomicInteger();
        while (c.getAndIncrement() < count) {
            result.add(tempList.get(random(tempList.size())));
        }
        return result;
    }

    /**
     * 根据权重从给定的Map中选择元素。
     *
     * @param map 一个包含键值对的Map，其中键代表元素，值代表该元素的权重。
     * @return 返回一个数组，包含根据权重选择的元素。具体的选法依赖于chooseArrayByWeight方法的实现。
     */
    public static int[] chooseMapByWeight(Map<Integer, Integer> map) {
        // 将Map中的键值对转换为int数组的列表，准备进行进一步处理
        List<int[]> collect = map.entrySet().stream().map(entry -> new int[]{entry.getKey(), entry.getValue()}).collect(Collectors.toList());
        return chooseArrayByWeight(collect);
    }

    /**
     * 按概率从列表中选择一个元素(数组最后一个元素为权重)
     */
    public static int[] chooseArrayByWeight(int[][] array) {
        List<int[]> collect = Arrays.stream(array).collect(Collectors.toList());
        return chooseArrayByWeight(collect);
    }

    public static int[] chooseArrayByWeight(List<int[]> list) {
        int total = list.stream().mapToInt(v -> v[v.length - 1]).sum();
        if (total <= 0) {
            return null;
        }
        int r = random(1, total);
        int s = 0;

        Collections.shuffle(list);
        for (int[] p : list) {
            s += p[p.length - 1];
            if (r <= s) {
                return p;
            }
        }
        return null;
    }


    /**
     * 按概率从列表中选择一个元素
     * 概率在数组第一位
     * 与上一个方法在最后一位相对
     */
    public static int[] chooseArrayByHeadWeight(int[][] array) {
        List<int[]> collect = Arrays.stream(array).collect(Collectors.toList());
        return chooseArrayByHeadWeight(collect);
    }

    public static int[] chooseArrayByHeadWeight(List<int[]> list) {
        int total = list.stream().mapToInt(v -> v[0]).sum();
        if (total <= 0) {
            return null;
        }
        int r = random(1, total);
        int s = 0;
        Collections.shuffle(list);
        for (int[] p : list) {
            s += p[0];
            if (r <= s) {
                return p;
            }
        }
        return null;
    }

    /**
     * 从List中根据权重取元素（第一个值为权重
     */
    public static int[] randomList(List<int[]> arr) {
        if (ArraysUtils.isEmpty(arr)) {
            return null;
        }
        int weight = arr.stream().mapToInt(v -> v[0]).sum();
        int random = ProbabilityUtils.random(1, weight);
        int temp = 0;
        for (int[] ar : arr) {
            temp += ar[0];
            if (random <= temp) {
                return ar;
            }
        }
        return null;
    }

    public static <T> List<T> randomFromList(List<T> arr, int num) {
        List<T> result = new ArrayList<>();

        if (ArraysUtils.isEmpty(arr)) {
            return result;
        }

        if (num >= arr.size()) {
            result.addAll(arr);
            return result;
        }

        List<Integer> indexes = randomFromSection(0, arr.size() - 1, num, false);

        for (Integer index : indexes) {
            result.add(arr.get(index));
        }

        return result;
    }

    /**
     * 从数组中根据权重取元素（第一个值为权重
     */
    public static int[] randomArr(int[][] arr) {
        return randomList(Arrays.stream(arr).collect(Collectors.toList()));
    }

    /**
     * 从数组中根据权重取元素 值为权重
     */
    public static int randomFromArr(int[] arr) {
        if (arr.length == 0) {
            return -1;
        }
        int weight = 0;
        for (int value : arr) {
            weight += value;
        }
        if (weight <= 0) {
            return -1;
        }
        int random = ProbabilityUtils.random(1, weight);
        int temp = 0;
        int index = 0;
        for (int value : arr) {
            temp += value;
            if (random <= temp) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * 从区间随机N个数字
     *
     * @param min     最小值
     * @param max     最大值
     * @param needNum 所需数量
     * @param repeat  是否重复 true 可重复, false不可重复
     */
    public static List<Integer> randomFromSection(int min, int max, int needNum, boolean repeat) {
        Set<Integer> pool = new HashSet<>();
        for (int i = min; i <= max; i++) {
            pool.add(i);
        }
        if (repeat) {
            return chooseByCount(pool, needNum);
        } else {
            return chooseByNum(pool, needNum);
        }
    }

    /**
     * 对象集分成两组
     *
     * @param resources 源集合
     * @param a         A组
     * @param b         B组
     */
    public static <T> void groupingList(List<T> resources, List<T> a, List<T> b) {
        int maxSize = resources.size();
        for (int i = 0; i < maxSize / 2; i++) {
            List<T> random = chooseByNum(resources, 2);
            a.add(random.get(0));
            b.add(random.get(1));
            resources.removeAll(random);
        }
    }

    public static boolean checkProbability(int probability, int sum) {
        return random(1, sum) <= probability;
    }


    public static void main(String[] args) {
        List<Integer> resources = new ArrayList<>();
        for (int i = 1; i <= 64; i++) {
            resources.add(i);
        }
        List<Integer> a = Lists.newArrayList();
        List<Integer> b = Lists.newArrayList();
        groupingList(resources, a, b);
        System.out.println(a);
        System.out.println(b);
    }
}
