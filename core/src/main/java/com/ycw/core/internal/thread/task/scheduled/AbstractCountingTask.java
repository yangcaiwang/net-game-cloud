package com.ycw.core.internal.thread.task.scheduled;

import com.ycw.core.cluster.property.PropertyConfig;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 带执行次数的抽象时效任务
 */
public abstract class AbstractCountingTask extends AbstractScheduledTask {

	/**
	 * 无穷多次数
	 */
	public static final byte UNLIMITED = -1;

	/**
	 * 最多可执行次数
	 */
	private final AtomicLong maxCount = new AtomicLong(UNLIMITED);

	/**
	 * 当前执行到第几次
	 */
	private final AtomicLong currentCount = new AtomicLong(0);

	/**
	 * 定时器指派的任务钩子
	 */
	private AtomicReference<ScheduledFuture<?>> future = new AtomicReference<ScheduledFuture<?>>(null);

	/**
	 * 获取最大可执行次数
	 * 
	 * @return
	 */
	public long getMaxCount() {

		return maxCount.get();
	}

	/**
	 * 获取当前第几次
	 * 
	 * @return
	 */
	public long getCurrentCount() {

		return currentCount.get();
	}

	/**
	 * 设置最大可执行次数
	 * 
	 * @param maxCount
	 *            如果是-1表示无限次,那么也不会记录当前执行了多少次
	 */
	public void setMaxCount(long maxCount) {

		if (maxCount < 1 && maxCount != UNLIMITED) {
			throw new RuntimeException("无效的执行次数,maxCount=" + maxCount);
		}
		this.maxCount.compareAndSet(this.maxCount.get(), maxCount);
	}

	/**
	 * 获取定时器指派的任务钩子
	 * 
	 * @return
	 */
	public ScheduledFuture<?> getFuture() {

		return future.get();
	}

	/**
	 * 设置定时器指派的任务钩子,只能在启动任务时获得,不需要调用也不能调用
	 * 
	 * @param future
	 */
	public void setFuture(ScheduledFuture<?> future) {

		this.future.compareAndSet(this.future.get(), future);
	}

	/**
	 * 关闭任务
	 * 
	 * @param mayInterruptIfRunning
	 */
	public void cancel(boolean mayInterruptIfRunning) {

		if (getFuture() != null && !getFuture().isCancelled()) {
			getFuture().cancel(mayInterruptIfRunning);
			if (PropertyConfig.isDebug()) {
				log.info("任务关闭:" + this);
			}
		}
	}

	/**
	 * 立刻关闭任务,如果正在执行也将被销毁
	 */
	public void cancel() {
		cancel(true);
	}

	@Override
	protected void exec() {

		try {
			exe();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (getMaxCount() != UNLIMITED) {
				if (currentCount.incrementAndGet() >= getMaxCount()) {
					cancel(true);
				}
			}
		}
	}

	/**
	 * 执行任务逻辑
	 * 
	 * @throws Exception
	 */
	protected abstract void exe() throws Exception;

}
