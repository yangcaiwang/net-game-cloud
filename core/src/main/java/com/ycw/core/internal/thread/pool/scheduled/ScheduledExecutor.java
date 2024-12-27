package com.ycw.core.internal.thread.pool.scheduled;

import com.ycw.core.internal.thread.NamedThreadFactory;
import com.ycw.core.internal.thread.task.AbstractCoreCallback;
import com.ycw.core.internal.thread.task.AbstractCoreTask;
import com.ycw.core.internal.thread.task.scheduled.AbstractCountingTask;
import com.ycw.core.internal.thread.task.scheduled.AbstractScheduledCallback;
import com.ycw.core.internal.thread.task.scheduled.AbstractScheduledTask;
import com.ycw.core.internal.thread.task.scheduled.AbstractTimingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * <延时周期线程池实现类>
 * <p>
 * ps: java并发包下自带的定时任务管理器
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ScheduledExecutor {

    private static final Logger log = LoggerFactory.getLogger(ScheduledExecutor.class);

    private static ScheduledExecutorService scheduledExecutorService;

    public static ScheduledExecutorService scheduler() {
        if (scheduledExecutorService == null) {
            synchronized (ScheduledExecutor.class) {
                if (scheduledExecutorService == null) {
                    scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() / 2 + 1, new NamedThreadFactory(ScheduledExecutor.class.getSimpleName()));
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
     */
    public static <E extends AbstractCountingTask> ScheduledFuture<?> scheduleAtFixedRate(E command, long initialDelay, long period, TimeUnit unit, int count) {
        ScheduledFuture<?> future = scheduler().scheduleAtFixedRate(command, initialDelay, period, unit);
        command.setMaxCount(count);
        command.setFuture(future);
        return future;
    }

    /**
     * 不保证不准时在initialDelay后每隔period执行,总共执行count次
     */
    public static <E extends AbstractCountingTask> ScheduledFuture<?> scheduleWithFixedDelay(E command, long initialDelay, long delay, TimeUnit unit, int count) {
        ScheduledFuture<?> future = scheduler().scheduleWithFixedDelay(command, initialDelay, delay, unit);
        command.setMaxCount(count);
        command.setFuture(future);
        return future;
    }

    /**
     * 保证准时在initialDelay后每隔period执行,在endTime结束任务
     */
    public static <E extends AbstractTimingTask> ScheduledFuture<?> scheduleAtFixedRate(E command, long initialDelay, long period, TimeUnit unit, long endTime) {
        ScheduledFuture<?> future = scheduler().scheduleAtFixedRate(command, initialDelay, period, unit);
        command.setEndTime(endTime);
        command.setFuture(future);
        return future;
    }

    /**
     * 不保证不准时在initialDelay后每隔period执行,在endTime结束任务
     */
    public static <E extends AbstractTimingTask> ScheduledFuture<?> scheduleWithFixedDelay(E command, long initialDelay, long delay, TimeUnit unit, long endTime) {
        ScheduledFuture<?> future = scheduler().scheduleWithFixedDelay(command, initialDelay, delay, unit);
        command.setEndTime(endTime);
        command.setFuture(future);
        return future;
    }

    /**
     * 保证准时在initialDelay后每隔period执行,
     */
    public static <R extends AbstractScheduledTask> ScheduledFuture<?> scheduleAtFixedRate(R command, long initialDelay, long period, TimeUnit unit) {
        return scheduler().scheduleAtFixedRate(command, initialDelay, period, unit);
    }

    /**
     * 不保证不准时在initialDelay后每隔period执行
     */
    public static <R extends AbstractScheduledTask> ScheduledFuture<?> scheduleWithFixedDelay(R command, long initialDelay, long delay, TimeUnit unit) {
        return scheduler().scheduleWithFixedDelay(command, initialDelay, delay, unit);
    }

    /**
     * 延迟delay后执行
     */
    public static <V, C extends AbstractScheduledCallback<V>> ScheduledFuture<?> schedule(C callable, long delay, TimeUnit unit) {
        return scheduler().schedule(callable, delay, unit);
    }

    /**
     * 延迟delay后执行
     */
    public static <R extends AbstractScheduledTask> ScheduledFuture<?> schedule(R command, long delay, TimeUnit unit) {
        return scheduler().schedule(command, delay, unit);
    }

    /**
     * 异步立刻执行,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
     */
    public static <R extends AbstractCoreTask> void execute(R command, Executor executor) {
        executor.execute(command);
    }

    /**
     * 异步立刻执行,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
     */
    public static <R extends AbstractCoreTask> void execute(R command) {
        execute(command, scheduler());
    }

    /**
     * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
     */
    public static <R extends AbstractCoreTask> Future<?> runAsync(R task, Executor executor) {
        return CompletableFuture.runAsync(task, executor);
    }

    /**
     * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
     */
    public static <R extends AbstractCoreTask> Future<?> runAsync(R task) {
        return runAsync(task, scheduler());
    }

    /**
     * 异步立刻执行,并得到回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
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
     */
    public static <V, C extends AbstractCoreCallback<V>> Future<V> supplyAsync(C task) {
        return supplyAsync(task, scheduler());
    }

    /**
     * 异步立刻执行,并得到给定的结果回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
     */
    public static <T, R extends AbstractCoreTask> Future<T> supplyAsync(R task, T result, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            task.run();
            return result;
        }, executor);
    }

    /**
     * 异步立刻执行,并得到给定的结果回调,调用该方法一定要谨慎,因为不确定逻辑会在哪条线程执行,只有不是不关注代码执行顺序和原子的逻辑才可以用
     */
    public static <T, R extends AbstractCoreTask> Future<T> supplyAsync(R task, T result) {
        return supplyAsync(task, result, scheduler());
    }
}
