
package com.common.module.internal.delay;

import com.common.module.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * <服务器定时任务类>
 * <p>
 * ps: 用于构建海量时效任务,内部最小时间单位是10毫秒
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServerTimerTask<Param extends Serializable> implements Comparable<ServerTimerTask<Param>> {

    private static final Logger log = LoggerFactory.getLogger(ServerTimerTask.class);

    private static final AtomicLong auto = new AtomicLong(System.nanoTime());

    /**
     * 构造一个延迟任务
     *
     * @param id     唯一标识
     * @param end    结束时间
     * @param param  携带的参数
     * @param action 到点执行的拉姆达表达式
     * @param max    -1表示无限多不可以是0
     */
    public static <Param extends Serializable> ServerTimerTask<Param> valueOf(String id, long end, Param param, Consumer<Param> action, long max) {

        Objects.requireNonNull(id, "id不可以是null");
        Objects.requireNonNull(action, "执行函数不可以是null");
        return new ServerTimerTask<Param>(id, System.currentTimeMillis(), end, param, action, max);
    }

    /**
     * 构造一个延迟任务
     *
     * @param id     唯一标识
     * @param end    结束时间
     * @param action 到点执行的拉姆达表达式
     */
    public static <Param extends Serializable> ServerTimerTask<Param> valueOf(String id, long end, Consumer<Param> action, long max) {

        return valueOf(id, end, null, action, max);
    }

    /**
     * 构造一个延迟任务
     *
     * @param end    结束时间
     * @param param  携带的参数
     * @param action 到点执行的拉姆达表达式
     */
    public static <Param extends Serializable> ServerTimerTask<Param> valueOf(long end, Param param, Consumer<Param> action, long max) {

        Objects.requireNonNull(action, "执行函数不可以是null");
        return valueOf(String.valueOf(auto.incrementAndGet()), end, param, action, max);
    }

    /**
     * 构造一个延迟任务
     *
     * @param end    结束时间
     * @param action 到点执行的拉姆达表达式
     */
    public static <Param extends Serializable> ServerTimerTask<Param> valueOf(long end, Consumer<Param> action, long max) {

        Objects.requireNonNull(action, "执行函数不可以是null");
        return valueOf(end, null, action, max);
    }

    /**
     * 任务唯一标识
     */
    private String id;

    /**
     * 任务开始时间
     */
    private AtomicLong begin = new AtomicLong(0L);

    /**
     * 任务携带的参数
     */
    private AtomicReference<Param> param = new AtomicReference<Param>(null);

    /**
     * 处理函数,提交任务时就需要把最终处理的过程提交进来
     */
    private AtomicReference<Consumer<Param>> action = new AtomicReference<Consumer<Param>>(null);

    /**
     * 任务结束时间
     */
    private AtomicLong end = new AtomicLong(0L);

    /**
     * 开始到结束相隔时间
     */
    private AtomicLong interval = new AtomicLong(0L);

    /**
     * 最多重复次数-1无限循环
     */
    private AtomicLong maxCount = new AtomicLong(0L);

    /**
     * 当前执行次数计数
     */
    private AtomicLong count = new AtomicLong(0L);

    /**
     * 是否正在执行
     */
    private AtomicBoolean running = new AtomicBoolean(false);

    public ServerTimerTask(String id, long begin, long end, Param param, Consumer<Param> action, long max) {
        super();
        if (!ServerTimer.getInstance().isRunning())
            throw new RuntimeException("ServerTimer not start");
        if (begin > end) {
            if (max == -1 || max > 1) {
                throw new RuntimeException(String.format("begin(%s) > end(%s)", begin, end));
            }
            log.error("task id:{} begin:{} > end:{}", id, begin, end, new RuntimeException());
            end = begin;
        }
        if (max <= 0 && max != -1)
            throw new RuntimeException(String.format("max(%s)", max));
        this.id = id;
        this.begin.set(begin);
        this.end.set(end);
        this.interval.set(end - begin);
        this.param.set(param);
        this.action.set(action);
        this.maxCount.set(max);
    }

    long addCount() {
        long nowCount = count.get();
        long newCount = count.incrementAndGet();
        log.debug("任务[{}]执行次数增加[{}]->[{}]", this, nowCount, newCount);
        return newCount;
    }

    boolean running() {
        return running.get();
    }

    boolean start() {
        return running.compareAndSet(false, true);
    }

    void stop() {
        running.compareAndSet(true, false);
    }

    void again() {
        log.debug("任务[{}]迭代前", this);
        long now = System.currentTimeMillis();
        begin.compareAndSet(begin.get(), now);
        end.compareAndSet(end.get(), now + interval.get());
        interval.compareAndSet(interval.get(), end.get() - begin.get());
        log.debug("任务[{}]迭代后", this);
    }

    public void updateAction(Param newParam, Consumer<Param> newAction) {
        log.debug("任务[{}]函数更新前", this);
        param.compareAndSet(param.get(), newParam);
        action.compareAndSet(action.get(), newAction);
        log.debug("任务[{}]函数更新后", this);
    }

    public void updateMaxCount(long newMaxCount) {
        long nowMaxCount = maxCount.get();
        maxCount.compareAndSet(maxCount.get(), newMaxCount);
        log.debug("任务[{}]修改最大执行次数[{}]->[{}]", this, nowMaxCount, maxCount.get());
    }

    public void lengthen(long duration, TimeUnit unit) {
        log.debug("任务[{}]延迟前", this);
        end.compareAndSet(end.get(), end.get() + unit.toMillis(duration));
        interval.compareAndSet(interval.get(), end.get() - begin.get());
        log.debug("任务[{}]延迟后", this);
    }

    public void cancel() {
        log.debug("任务[{}关闭前", this);
        param.compareAndSet(param.get(), null);
        action.compareAndSet(action.get(), null);
        log.debug("任务[{}]关闭后", this);
    }

    public long getMaxCount() {
        return maxCount.get();
    }

    public long getCount() {
        return count.get();
    }

    public long getInterval() {
        return interval.get();
    }

    public String getId() {
        return id;
    }

    public long getBegin() {
        return begin.get();
    }

    public Param getParam() {
        return param.get();
    }

    public Consumer<Param> getAction() {
        return action.get();
    }

    public long getEnd() {
        return end.get();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBegin(AtomicLong begin) {
        this.begin = begin;
    }

    public void setParam(AtomicReference<Param> param) {
        this.param = param;
    }

    public void setAction(AtomicReference<Consumer<Param>> action) {
        this.action = action;
    }

    public void setEnd(AtomicLong end) {
        this.end = end;
    }

    public void setInterval(AtomicLong interval) {
        this.interval = interval;
    }

    public void setMaxCount(AtomicLong maxCount) {
        this.maxCount = maxCount;
    }

    public void setCount(AtomicLong count) {
        this.count = count;
    }

    public AtomicBoolean getRunning() {
        return running;
    }

    public void setRunning(AtomicBoolean running) {
        this.running = running;
    }

    @Override
    public int compareTo(ServerTimerTask<Param> o) {
        return new Long(this.getEnd()).compareTo(new Long(o.getEnd()));
    }

    @Override
    public String toString() {
        return StringUtils.toString(this);
    }

}
