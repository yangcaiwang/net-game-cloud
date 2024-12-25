
package com.common.module.internal.thread;

import com.common.module.util.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <线程工场实现类>
 * <p>
 * ps: 异步结果,默认1秒后视为超时
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class NamedThreadFactory implements ThreadFactory {

    private final AtomicInteger incr = new AtomicInteger();

    private final ThreadGroup threadGroup;

    private final String threadName;

    private final long stackSize;

    private final boolean daemon;

    /**
     * 构造线程工厂
     *
     * @param threadGroup 所属线程组
     * @param threadName  线程名
     * @param stackSize   线程栈大小,根据操作系统决定,之所以这里不公开就是屏蔽修改线程栈大小的入口
     * @param daemon      是否守护线程
     */
    private NamedThreadFactory(ThreadGroup threadGroup, String threadName, long stackSize, boolean daemon) {
        super();

        Validate.isTrue(!StringUtils.isEmpty(threadName), "thread name[%s] can not be null or empty", threadName);
        Validate.isTrue(stackSize == 0, "stackSize must set 0", stackSize);

        this.threadGroup = threadGroup;
        this.threadName = threadName;
        this.stackSize = stackSize;
        this.daemon = daemon;
    }

    public NamedThreadFactory(ThreadGroup threadGroup, String threadName, boolean daemon) {
        this(threadGroup, threadName, 0, daemon);
    }

    public NamedThreadFactory(String threadName, boolean daemon) {
        this(null, threadName, daemon);
    }

    public NamedThreadFactory(ThreadGroup threadGroup, String threadName) {
        this(threadGroup, threadName, false);
    }

    public NamedThreadFactory(String threadName) {
        this(null, threadName);
    }

    public NamedThreadFactory() {
        this(null);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(threadGroup, r, StringUtils.merged(threadName, incr.incrementAndGet()), stackSize);
        thread.setDaemon(daemon);
        org.slf4j.LoggerFactory.getLogger(NamedThreadFactory.class).info("new thread [{}.{}.{}]", thread, thread.isDaemon(), thread.getId());
        return thread;
    }
}
