package com.ycw.core.internal.thread.pool.actor;

import com.google.common.collect.Maps;
import com.ycw.core.internal.random.ProbabilityUtils;
import com.ycw.core.internal.thread.task.ICoreTask;
import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.internal.thread.task.linked.PlayerAbstractLinkedTask;
import com.ycw.core.util.HashCodeUtil;
import com.ycw.core.util.StringUtils;
import com.ycw.core.util.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <actor线程池实现类>
 * <p>
 * ps: 绑定线程的任务池（模拟actor模型） 线程间任务队列不可见，每个线程有自己的任务队列，可根据需求实现单线程模型的业务场景
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
final public class ActorThreadPoolExecutor implements Executor, ICoreTask {
    private static final Logger log = LoggerFactory.getLogger(ActorThreadPoolExecutor.class);

    @Override
    public void execute(@NotNull Runnable runnable) {
        boolean b = false;
        try {
            if (runnable instanceof AbstractLinkedTask) {
                AbstractLinkedTask exe = (AbstractLinkedTask) runnable;
                if (exe.cutline()) {
                    b = addFirst(exe.getIdentity(), exe);
                } else {
                    b = addLast(exe.getIdentity(), exe);
                }
            } else if (runnable instanceof PlayerAbstractLinkedTask) {
                PlayerAbstractLinkedTask exe = (PlayerAbstractLinkedTask) runnable;
                b = addLast(exe.getIdentity(), exe);
            } else {
                b = addLast(runnable);
            }
            if (!b) {
                throw new RuntimeException("执行异步任务失败:" + runnable);
            }
        } catch (Exception e) {
            log.error(e.getMessage() + ":" + runnable, e);
        }
    }

    public int slowly;
    public int busily;
    private ThreadGroup threadGroup;
    private String threadName;
    private ActorThread[] actor_thread_array;
    private AtomicLong count = new AtomicLong();

    // 超时任务时间设置，设置了参数，超过限定时间没处理完，就放弃任务，防止线程死循环挂起（战斗）
    private int timeOutSec;

    public ActorThreadPoolExecutor(ThreadGroup threadGroup, String threadName, int num, int slowly, int busily, int timeOutSec) {
        this.slowly = slowly <= 0 ? SLOWLY : slowly;
        this.busily = busily <= 0 ? BUSILY : busily;
        this.threadGroup = threadGroup;
        this.threadName = threadName;
        this.actor_thread_array = new ActorThread[num];
        this.timeOutSec = timeOutSec;
    }

    public ActorThreadPoolExecutor(ThreadGroup threadGroup, String threadName, int num) {
        this(threadGroup, threadName, num, 0, 0, 0);
    }

    public ActorThreadPoolExecutor(String name, int num) {
        this(null, name, num);
    }

    ActorThread actorThread(int index) {
        if (actor_thread_array[index] == null) {
            synchronized (this) {
                if (actor_thread_array[index] == null) {
                    actor_thread_array[index] = new ActorThread(threadGroup, StringUtils.merged(threadName, (index + 1)), slowly, busily, timeOutSec);
                }
            }
        }
        return actor_thread_array[index];
    }

    int getActorIndex(Object hash) {
        return hash == null ? ProbabilityUtils.random(actor_thread_array.length) : HashCodeUtil.absHashCode(hash) % actor_thread_array.length;
    }

    ActorThread getActorThread(Object hash) {
        return actorThread(getActorIndex(hash));
    }

    public boolean addLast(Object hash, Runnable runner) {
        if (getActorThread(hash).addLast(runner)) {
            count.incrementAndGet();
            return true;
        }
        throw new RuntimeException(hash + "," + runner);
    }

    public boolean addLast(Runnable runner) {
        return this.addLast(null, runner);
    }

    public boolean addFirst(Object hash, Runnable runner) {
        if (getActorThread(hash).addFirst(runner)) {
            count.incrementAndGet();
            return true;
        }
        throw new RuntimeException(hash + "," + runner);
    }

    public boolean addFirst(Runnable runner) {
        return this.addFirst(null, runner);
    }

    // ---------------------------------------------------------------------------------------

    public Map<String, Integer> getNumberOfPendingExecutions() {
        Map<String, Integer> map = Maps.newConcurrentMap();
        ActorThread[] _threads = actor_thread_array;
        for (ActorThread actorThread : _threads) {
            if (actorThread != null)
                map.putIfAbsent(actorThread.thread.getName(), actorThread.getNumberOfPendingExecutions());
        }
        return map;
    }

    public Map<String, Long> getNumberOfExecutions() {
        Map<String, Long> map = Maps.newConcurrentMap();
        ActorThread[] _threads = actor_thread_array;
        for (ActorThread actorThread : _threads) {
            if (actorThread != null)
                map.putIfAbsent(actorThread.thread.getName(), actorThread.getNumberOfExecutions());
        }
        return map;
    }

    boolean isStopped() {
        for (ActorThread actorThread : actor_thread_array) {
            if (actorThread != null && !actorThread.isStopped()) {
                return false;
            }
        }
        return true;
    }

    public void shutdown() {
        log.warn("shutdown 线程池 [{}],各子线程待执行任务[{}]", threadName, getNumberOfPendingExecutions());
        for (ActorThread taskThread : actor_thread_array) {
            if (taskThread != null)
                taskThread.shutdown();
        }
        log.info("停线程完毕执行完毕！！！！");
    }

    public void shutdownFully() {
        shutdown();
        while (!isStopped()) {
            SystemUtils.sleep(1, TimeUnit.SECONDS);
        }
        log.warn("线程池 [{}] is stopped,总共执行了[{}]次任务,各子线程已执行任务[{}]", threadName, count, getNumberOfExecutions());
    }
}
