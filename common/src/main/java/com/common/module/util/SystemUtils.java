
package com.common.module.util;

import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SystemUtils {
	private static final AtomicReference<String> pid = new AtomicReference<String>(null);
	/**
	 * 获取项目根目录
	 */
	public static String getServerHome() {
		return System.getProperty("user.dir");
	}

	/**
	 * 获取当前的主线程id(进程id)
	 */
	public static String getPid() {
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}

	/**
	 * 是否为win系统
	 */
	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	/**
	 * 是否为linux系统
	 */
	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}

	/**
	 * 是否为linux系统
	 */
	public static boolean isMac() {
		return System.getProperty("os.name").toLowerCase().contains("mac");
	}

	public static String getPID() {

		if (pid.get() == null) {
			pid.compareAndSet(null, ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
		}
		return pid.get();
	}
	public static void sleep(long timeout, TimeUnit unit) {
		try {
			unit.sleep(timeout);
		} catch (InterruptedException e) {
			LoggerFactory.getLogger("thread.sleep.InterruptedException").error(e.getMessage(), e);
			return;
		}
	}

	/**
	 * 获取内存使用率
	 */
	public static double memoryUsed() {
		long freeMemory = Runtime.getRuntime().freeMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		double d = (totalMemory - freeMemory * 1.0d) / totalMemory;
		BigDecimal bg = new BigDecimal(d);
		double result = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return result;
	}

	public static Map<Thread, StackTraceElement[]> threadStackTraces() {
		return Thread.getAllStackTraces();
	}

	public static Set<Thread> threadSet() {
		Map<Thread, StackTraceElement[]> maps = threadStackTraces();
		return maps.keySet();
	}

	public static Collection<StackTraceElement[]> threadValues() {
		Map<Thread, StackTraceElement[]> maps = threadStackTraces();
		return maps.values();
	}
}
