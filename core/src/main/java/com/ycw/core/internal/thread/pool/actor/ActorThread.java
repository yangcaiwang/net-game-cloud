package com.ycw.core.internal.thread.pool.actor;

import com.ycw.core.internal.thread.task.AbstractCoreTask;
import com.ycw.core.internal.thread.task.ICoreTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <actor线程任务实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
final class ActorThread implements Runnable, ICoreTask {

	static final Logger log = LoggerFactory.getLogger(ActorThread.class);

	private final Object lock = new Object();
	final int slowly;
	final int busily;
	final Thread thread;

	volatile boolean run = true;
	volatile boolean shutdown = false;

	LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	long count;

	long countTime;

	ActorThread(ThreadGroup threadGroup, String threadName, int slowly, int busily, int timeOutSec) {
		this.slowly = slowly <= 0 ? SLOWLY : slowly;
		this.busily = busily <= 0 ? BUSILY : busily;
		this.thread = new Thread(threadGroup, this, threadName);
		this.thread.start();
	}
	int getNumberOfPendingExecutions() {
		return tasks.size();
	}

	long getNumberOfExecutions() {
		return count;
	}

	boolean isStopped() {
		return !run;
	}

	boolean addLast(Runnable exe) {
		synchronized (lock) {
			try {
				tasks.add(exe);
			} finally {
				lock.notifyAll();
			}
		}
		return true;
	}

	boolean addFirst(Runnable exe) {
		synchronized (lock) {
			try {
				addLast(exe);
			} finally {
				lock.notifyAll();
			}
		}
		return true;
	}

	void shutdown() {
		synchronized (lock) {
			try {
				shutdown = true;
			} finally {
				lock.notifyAll();
			}
		}
	}

	@Override
	public void run() {
		while (run) {
			boolean sd = checkShutdown();
			if (sd) {
				break;
			}
			execTasks();
		}
		// 执行剩余任务
		executeRemainderTasks();
	}

	boolean checkShutdown() {
		if (shutdown) {
			run = false;
			log.info("线程 [{}] stopped!总共执行了[{}]次任务 countTime={}", thread.getName(), count, countTime);
		}
		return shutdown;
	}

	void execTasks() {
		int countTask = tasks.size();
		if (countTask == 0) {
			synchronized (lock) {
				try {
					lock.wait();
				} catch (InterruptedException e) {
					log.error("线程【{}】 InterruptedException", thread.getName());
					Thread.currentThread().interrupt();
				}
			}
			return;
		}
		if (countTask >= busily)
			log.warn("BUSILY:线程[{}]待执行任务个数[{}]", thread.getName(), countTask);
		for (int i = 0; i < countTask; i++) {
			Runnable poll = tasks.poll();
			if (poll == null) {
				break;
			}
			try {
				poll.run();
			} catch (Exception e) {
				log.error(String.format("actorTask run err,task=[%s],err=[%s] count=[%d]", poll, e.getMessage(), tasks.size()), e);
			} finally {
				count++;
			}
		}

	}

	private void executeRemainderTasks() {
		if (!tasks.isEmpty()) {
			List<Runnable> remainderTasks = new LinkedList<>();
			tasks.drainTo(remainderTasks);
			if (!remainderTasks.isEmpty()) {
				log.info("线程 [{}] 还有剩余任务未执行，数量：[{}]", thread.getName(), remainderTasks.size());
				// 此处可以调用 exe 方法处理剩余任务，但需要保证 exe 方法线程安全且处理正确
				exe(remainderTasks);
			}
		}
	}

	void exe(List<Runnable> exeTasks) {
		if (exeTasks.size() >= busily)
			log.warn("BUSILY:线程[{}]待执行任务个数[{}]", thread.getName(), exeTasks.size());
		for (Runnable runner : exeTasks) {
			long begin = 0;
			try {
				if (!(runner instanceof AbstractCoreTask))
					begin = System.currentTimeMillis();
				runner.run();

			} catch (Exception e) {
				log.error(String.format("actorTask run err,task=[%s],err=[%s]", runner, e.getMessage()), e);
			} finally {
				count++;
				log.debug("线程[{}]执行任务[{}]完毕", thread.getName(), runner);
				if (begin > 0) {
					long end = System.currentTimeMillis();
					long interval = end - begin;
					countTime += interval;
					if (interval > slowly) {
						log.warn("SLOWLY:线程[{}]执行任务[{}]耗时[{}]ms", thread.getName(), runner, interval);
					}
				}
			}
		}
	}
}
