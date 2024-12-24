
package com.common.module.internal.thread.task.scheduled;

import com.common.module.util.DateUnit;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 带时间可结束的抽象时效任务
 */
public abstract class AbstractTimingTask extends AbstractScheduledTask {

	public static final byte UNLIMITED = -1;

	private final AtomicLong endTime = new AtomicLong(UNLIMITED);

	private AtomicReference<ScheduledFuture<?>> future = new AtomicReference<ScheduledFuture<?>>(null);

	/**
	 * 设置任务结束时间
	 * 
	 * @param overTime
	 *            如果是-1表示不结束
	 */
	public void setEndTime(long overTime) {

		if (overTime < System.currentTimeMillis() && overTime != UNLIMITED) {
			throw new RuntimeException("不能提交一个过期的时间作为任务结束时间,overTime=" + DateUnit.timeFormat(overTime));
		}
		this.endTime.compareAndSet(this.endTime.get(), overTime);
	}

	public long getEndTime() {

		return endTime.get();
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
			log.info("任务关闭:" + this);
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
			if (endTime.get() != UNLIMITED) {
				if (System.currentTimeMillis() >= endTime.get()) {
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
