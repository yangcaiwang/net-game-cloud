
package com.ycw.core.internal.delay;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ycw.core.util.CollectionUtils;
import com.ycw.core.util.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;

/**
 * <服务器时效任务管理器实现类>
 * <p>
 * ps: 散列保存,按执行时按先后顺序取
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServerTimerTaskQueue {

    private static final Logger log = LoggerFactory.getLogger(ServerTimerTaskQueue.class);

    private static ServerTimerTaskQueue instance;

    public static ServerTimerTaskQueue getInstance() {
        if (instance == null) {
            synchronized (ServerTimerTaskQueue.class) {
                if (instance == null) {
                    instance = new ServerTimerTaskQueue();
                }
            }
        }
        return instance;
    }

    final Map<String, ServerTimerTask> taskMap = Maps.newConcurrentMap();

    /**
     * 获取一个延迟执行的任务
     */
    public ServerTimerTask getDelayTask(String id) {
        return taskMap.get(id);
    }

    /**
     * 获取一批延迟执行的任务
     */
    public List<ServerTimerTask> getAllDelayTasks(Predicate<? super ServerTimerTask> predicate) {
        Validate.notNull(predicate);
        if (taskMap.isEmpty())
            return Collections.emptyList();
        List<ServerTimerTask> list = Lists.newArrayList();
        taskMap.values().forEach(e -> {
            if (predicate.test(e)) {
                list.add(e);
            }
        });
        if (!list.isEmpty())
            Collections.sort(list, (t1, t2) -> Long.valueOf(t1.getEnd()).compareTo(Long.valueOf(t2.getEnd())));
        return list;
    }

    /**
     * 获取全部延迟执行的任务
     */
    public List<ServerTimerTask> getAllDelayTasks() {
        if (taskMap.isEmpty())
            return Collections.emptyList();
        List<ServerTimerTask> list = Lists.newArrayList(taskMap.values());
        Collections.sort(list, (t1, t2) -> Long.valueOf(t1.getEnd()).compareTo(Long.valueOf(t2.getEnd())));
        return list;
    }

    /**
     * 删除一个延迟执行任务
     */
    public ServerTimerTask removeDelayTask(String id) {
        return taskMap.remove(id);
    }

    /**
     * 删除一批延迟执行的任务
     */
    public List<ServerTimerTask> removeDelayTasks(Predicate<? super ServerTimerTask> predicate) {
        Validate.notNull(predicate);
        if (taskMap.isEmpty())
            return Collections.emptyList();
        List<ServerTimerTask> list = Lists.newArrayList();
        taskMap.values().removeIf(e -> {
            if (predicate.test(e)) {
                list.add(e);
                return true;
            }
            return false;
        });
        if (!list.isEmpty())
            Collections.sort(list, (t1, t2) -> Long.valueOf(t1.getEnd()).compareTo(Long.valueOf(t2.getEnd())));
        return list;
    }

    /**
     * 添加一个延迟任务 如果队列中存在相同的人会被弹出来并返回
     */
    public ServerTimerTask addDelayTask(ServerTimerTask task) {
        Objects.requireNonNull(task, "不可以添加null任务");
        log.debug("添加延迟任务[{}]", task);
        ServerTimerTask pop = (ServerTimerTask) taskMap.put(task.getId(), task);
        if (pop != null)
            log.info("延迟任务[{}]被[{}]替换", pop, task);
        return pop;
    }

    /**
     * 添加一批延迟任务
     */
    public Set<ServerTimerTask> addDelayTasks(Collection<ServerTimerTask> tasks) {
        Validate.isTrue(!CollectionUtils.isEmpty(tasks));
        log.debug("添加延迟任务[{}]", StringUtils.toString(tasks));
        Set<ServerTimerTask> set = Sets.newHashSet();
        for (ServerTimerTask task : tasks) {
            ServerTimerTask pop = addDelayTask(task);
            if (pop != null) {
                set.add(pop);
                log.debug("延迟任务[{}]被[{}]替换", pop, task);
            }
        }
        return set;
    }
}
