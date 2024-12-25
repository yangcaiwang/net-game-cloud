package com.common.module.internal.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <事件对象池实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class EventObjectPool {

    private static Logger logger = LoggerFactory.getLogger(EventObjectPool.class);
    private static Map<Class<?>, ConcurrentLinkedQueue<Object>> cacheEventMap = new ConcurrentHashMap<>();

    public static <T> T getEventObjFromPool(Class<T> cls) {
        ConcurrentLinkedQueue<Object> abstractEvents = cacheEventMap.computeIfAbsent(cls, v -> new ConcurrentLinkedQueue<>());
        T event = (T) abstractEvents.poll();
        if (event == null) {
            try {
                event = cls.newInstance();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return event;
    }

    public static <T> void collectEventObj(T event) {
        ConcurrentLinkedQueue<Object> abstractEvents = cacheEventMap.get(event.getClass());
        if (abstractEvents != null && abstractEvents.size() < 3) {
            abstractEvents.add(event);
        }
    }
}
