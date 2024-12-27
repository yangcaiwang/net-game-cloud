package com.ycw.core.internal.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <异步线程执行结果抽象类>
 * <p>
 * ps: 异步结果,默认1秒后视为超时
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AsyncResult<V> {

    private static final Logger log = LoggerFactory.getLogger(AsyncResult.class);

    final long time;
    AtomicLong begin = new AtomicLong();
    AtomicLong end = new AtomicLong();
    Throwable assertion;
    AtomicReference<V> result = new AtomicReference<>();

    public AsyncResult() {
        this(1, TimeUnit.SECONDS);
    }

    public AsyncResult(int duration, TimeUnit unit) {
        super();
        begin.compareAndSet(0L, System.currentTimeMillis());
        assertion = new Throwable();
        time = unit.toMillis(duration);
    }

    /**
     * 完成任务并设置结果对象
     */
    public void finish(V result) {
        this.result.compareAndSet(null, result);
        this.end.compareAndSet(0L, System.currentTimeMillis());
        if (this.end.get() - this.begin.get() > this.time) {
            log.error("AsyncResult {} executed time too long !", this);
        }
        try {
            this.complete();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取结果
     */
    public V getResult() {
        return result.get();
    }

    /**
     * 表明异步执行完成，需要手动调用finish并传入结果
     */
    protected abstract void complete() throws Exception;

    @Override
    public String toString() {
        StackTraceElement[] stackTraceElements = assertion.getStackTrace();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stackTraceElements.length; i++) {
            StackTraceElement stackTraceElement = stackTraceElements[i];
            builder.append(String.format("%s.%s(%d)", stackTraceElement.getClassName(), stackTraceElement.getMethodName(), stackTraceElement.getLineNumber()));
            if (i < stackTraceElements.length - 1)
                builder.append("\n");
        }
        return getClass().getSimpleName() + ":{result:" + result + ",begin:" + begin + ",end:" + end + ",time:" + time + ",timeUsed:" + (end.get() - begin.get()) + " ms" + ",\n调用栈:" + builder.toString() + "}";
    }
}
