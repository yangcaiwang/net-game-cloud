package com.common.module.internal.thread.pool.scheduled;


import com.common.module.internal.thread.NamedThreadFactory;
import com.common.module.internal.thread.task.scheduled.AbstractCountingTask;
import com.common.module.internal.thread.task.scheduled.AbstractScheduledTask;
import com.common.module.internal.thread.task.AbstractCoreCallback;
import com.common.module.internal.thread.task.AbstractCoreTask;
import com.common.module.internal.thread.task.scheduled.AbstractScheduledCallback;
import com.common.module.internal.thread.task.scheduled.AbstractTimingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * java并发包下自带的定时任务管理器
 */
public class ScheduledExecutorServiceScheduler {

	private static final Logger log = LoggerFactory.getLogger(ScheduledExecutorServiceScheduler.class);

	private static ScheduledExecutorService scheduledExecutorService;

	public static ScheduledExecutorService scheduler() {
		if (scheduledExecutorService == null) {
			synchronized (ScheduledExecutorServiceScheduler.class) {
				if (scheduledExecutorService == null) {
					scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2 + 1, new NamedThreadFactory(ScheduledExecutorServiceScheduler.class.getSimpleName()));
				}
			}
		}
		return scheduledExecutorService;
	}

	public static boolean isShutdown() {

		return scheduler().isShutdown();
	}

	public static void shutdown() {

		scheduler().shutdown();
	}

	public static List<Runnable> shutdownNow() {

		return scheduler().shutdownNow();
	}

	/**
	 * 保证准时在initialDelay后每隔period执行,总共执行count次
	 * 
	 * @param command
	 * @param initialDelay
	 * @param period
	 * @param unit
	 * @param count
	 * @return
	 */
	public static <E extends AbstractCountingTask> ScheduledFuture<?> scheduleAtFixedRate(E command, long initialDelay, long period, TimeUnit unit, int count) {
		ScheduledFuture<?> future = scheduler().scheduleAtFixedRate(command, initialDelay, period, unit);
		command.setMaxCount(count);
		command.setFuture(future);
		return future;
	}

	/**
	 * 不保证不准时在initialDelay后每隔period执行,总共执行count次
	 * 
	 * @param command
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @param count
	 * @return
	 */
	public static <E extends AbstractCountingTask> ScheduledFuture<?> scheduleWithFixedDelay(E command, long initialDelay, long delay, TimeUnit unit, int count) {
		ScheduledFuture<?> future = scheduler().scheduleWithFixedDelay(command, initialDelay, delay, unit);
		command.setMaxCount(count);
		command.setFuture(future);
		return future;
	}

	/**
	 * 保证准时在initialDelay后每隔period执行,在endTime结束任务
	 * 
	 * @param command
	 * @param initialDelay
	 * @param period
	 * @param unit
	 * @param endTime
	 * @return
	 */
	public static <E extends AbstractTimingTask> ScheduledFuture<?> scheduleAtFixedRate(E command, long initialDelay, long period, TimeUnit unit, long endTime) {
		ScheduledFuture<?> future = scheduler().scheduleAtFixedRate(command, initialDelay, period, unit);
		command.setEndTime(endTime);
		command.setFuture(future);
		return future;
	}

	/**
	 * 不保证不准时在initialDelay后每隔period执行,在endTime结束任务
	 * 
	 * @param command
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @param endTime
	 * @return
	 */
	public static <E extends AbstractTimingTask> ScheduledFuture<?> scheduleWithFixedDelay(E command, long initialDelay, long delay, TimeUnit unit, long endTime) {
		ScheduledFuture<?> future = scheduler().scheduleWithFixedDelay(command, initialDelay, delay, unit);
		command.setEndTime(endTime);
		command.setFuture(future);
		return future;
	}

	/**
	 * 保证准时在initialDelay后每隔period执行,
	 * 
	 * @param command
	 * @param initialDelay
	 * @param period
	 * @param unit
	 * @return
	 */
	public static <R extends AbstractScheduledTask> ScheduledFuture<?> scheduleAtFixedRate(R command, long initialDelay, long period, TimeUnit unit) {
		return scheduler().scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	/**
	 * 不保证不准时在initialDelay后每隔period执行
	 * 
	 * @param command
	 * @param initialDelay
	 * @param delay
	 * @param unit
	 * @return
	 */
	public static <R extends AbstractScheduledTask> ScheduledFuture<?> scheduleWithFixedDelay(R command, long initialDelay, long delay, TimeUnit unit) {
		return scheduler().scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

	/**
	 * 延迟delay后执行
	 * 
	 * @param callable
	 * @param delay
	 * @param unit
	 * @return
	 */
	public static <V, C extends AbstractScheduledCallback<V>> ScheduledFuture<?> schedule(C callable, long delay, TimeUnit unit) {
		return scheduler().schedule(callable, delay, unit);
	}

	/**
	 * 延迟delay后执行
	 * 
	 * @param command
	 * @param delay
	 * @param unit
	 * @return
	 */
	public static <R extends AbstractScheduledTask> ScheduledFuture<?> schedule(R command, long delay, TimeUnit unit) {
		return scheduler().schedule(command, delay, unit);
	}

	/**
	 * 异步立刻执行,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 * 
	 * @param command
	 */
	public static <R extends AbstractCoreTask> void execute(R command, Executor executor) {
		executor.execute(command);
	}

	/**
	 * 异步立刻执行,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 * 
	 * @param command
	 */
	public static <R extends AbstractCoreTask> void execute(R command) {
		execute(command, scheduler());
	}

	/**
	 * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 *
	 * @param task
	 * @return
	 */
	public static <R extends AbstractCoreTask> Future<?> runAsync(R task, Executor executor) {
		return CompletableFuture.runAsync(task, executor);
	}

	/**
	 * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 *
	 * @param task
	 * @return
	 */
	public static <R extends AbstractCoreTask> Future<?> runAsync(R task) {
		return runAsync(task, scheduler());
	}

	/**
	 * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 *
	 * @param task
	 * @return
	 */
	public static <V, C extends AbstractCoreCallback<V>> Future<V> supplyAsync(C task, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return task.call();
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return null;
			}
		}, executor);
	}

	/**
	 * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 *
	 * @param task
	 * @return
	 */
	public static <V, C extends AbstractCoreCallback<V>> Future<V> supplyAsync(C task) {
		return supplyAsync(task, scheduler());
	}

	/**
	 * 异步立刻执行,并得到给定的结果回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 *
	 * @param task
	 * @param result
	 * @return
	 */
	public static <T, R extends AbstractCoreTask> Future<T> supplyAsync(R task, T result, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			task.run();
			return result;
		}, executor);
	}

	/**
	 * 异步立刻执行,并得到给定的结果回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
	 *
	 * @param task
	 * @param result
	 * @return
	 */
	public static <T, R extends AbstractCoreTask> Future<T> supplyAsync(R task, T result) {
		return supplyAsync(task, result, scheduler());
	}

}