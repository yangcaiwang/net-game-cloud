package com.common.module.internal.thread.pool.actor;

import com.common.module.internal.thread.task.ICoreTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <单任务定时actor线程任务实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public final class TimerActorThread implements Runnable, ICoreTask {

    static final Logger log = LoggerFactory.getLogger(TimerActorThread.class);

    final Thread thread;

    volatile boolean run = true;
    volatile boolean shutdown = false;

    final Runnable task;

    int timeOutSec;

    long countTime;

    public TimerActorThread(String threadName, int timeOutSec, Runnable task) {
        this.timeOutSec = timeOutSec;
        this.task = task;
        this.thread = new Thread(this, threadName);
        this.thread.start();
    }


    public boolean isStopped() {
        return !run;
    }

    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            try {
                this.notifyAll();
            } finally {
            }
        }
    }

    @Override
    public void run() {
        while (run) {
            if (shutdown) {
                run = false;
                log.warn("线程 [{}] stopped! countTime={}", thread.getName(), countTime);
                break;
            }
            synchronized (this) {
                try {
                    this.wait(this.timeOutSec);
                } catch (InterruptedException e) {
                    log.error("线程【{}】 InterruptedException", thread.getName());
                }
            }
            execute();
        }

        execute();
    }

    public void execute() {
        try {
            task.run();
        } catch (Exception e) {
            log.error(String.format("actorTask run err,err=[%s] ", e.getMessage()), e);
        } finally {
        }
    }
}
