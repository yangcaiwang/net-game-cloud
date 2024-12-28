
package com.ycw.core.internal.event;

import com.google.common.collect.Maps;
import com.ycw.core.internal.event.annotation.EventSubscriber;
import com.ycw.core.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.ycw.core.internal.thread.task.linked.AbstractLinkedTask;
import com.ycw.core.util.AnnotationUtil;
import com.ycw.core.util.ClassUtils;
import com.ycw.core.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executor;

/**
 * <公共事件实现类>
 * <p>
 * ps:
 * 事件管理,根据guava事件机制重写的,支持同步和异步两种事件发布,并且异步发布事件可以保证相同事件的线性执行,
 * 事件消费者不可以是父类的方法,避免在父/子类重复监听相同事件,一个监听者不可以多次监听同一个事件,避免执行重复逻辑出现不可知异常
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class EventBusesImpl {

    private static final Logger log = LoggerFactory.getLogger(EventBusesImpl.class);

    private volatile static EventBusesImpl instance;

    public static EventBusesImpl getInstance() {
        if (instance == null) {
            synchronized (EventBusesImpl.class) {
                if (instance == null) {
                    instance = new EventBusesImpl();
                }
            }
        }
        return instance;
    }

    private final Executor executor;

    // {eventClass:{method:Observer}}
    private final Map<Class<? extends AbstractEvent>, Map<Method, AbstractEventObserver>> events = Maps.newConcurrentMap();

    private EventBusesImpl() {
        executor = new ActorThreadPoolExecutor("event", Runtime.getRuntime().availableProcessors() / 2 + 1);
    }

    <T extends AbstractEventObserver, E extends AbstractEvent> Map<Class<E>, Method> methods(T observer) {

        return methods(observer.getClass());
    }

    /**
     * 一个观察者(类)不可以在两处(方法)监听同一个事件(方法参数)
     *
     * @param observerClz 事件观察者
     */
    <T extends AbstractEventObserver, E extends AbstractEvent> Map<Class<E>, Method> methods(Class<T> observerClz) {

        Map<Class<E>, Method> result = Maps.newConcurrentMap();
        Method[] methods = observerClz.getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            EventSubscriber subscriber = AnnotationUtil.findAnnotation(method, EventSubscriber.class);
            if (subscriber != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length != 1)
                    throw new RuntimeException("事件消费方法参数错误:" + method.toGenericString());
                Class<?> eClz = parameterTypes[0];
                if (!ClassUtils.isAssignableFrom(AbstractEvent.class, eClz))
                    throw new RuntimeException("事件消费方法参数错误:" + method.toGenericString());
                Class<E> eventClazz = (Class<E>) eClz;// 事件消费方法只有一个参数
                Method old = result.put(eventClazz, method);
                if (old != null)
                    throw new RuntimeException(String.format("事件监听者[%s],重复监听事件[%s],method=[%s]", observerClz, eventClazz, method));
            }
        }
        return result;
    }

    /**
     * 注册事件消费者
     *
     * @param observer 事件观察者
     */
    <T extends AbstractEventObserver, E extends AbstractEvent> void register(T observer) {

//        boolean isScript = Scripts.isScript(observer);
//        if (isScript)
        log.info("add event observer  [{}] of script", observer);
        Map<Class<E>, Method> methods = methods(observer);
        for (Entry<Class<E>, Method> entry : methods.entrySet()) {
            Class<E> eventClazz = entry.getKey();
            Method method = entry.getValue();
            Map<Method, AbstractEventObserver> listeners = events.computeIfAbsent(eventClazz, v -> Maps.newConcurrentMap());
//            if (isScript) {
            listeners.forEach((m, o) -> {
                if (m.toGenericString().equals(method.toGenericString()))
                    log.warn("script observer[{}.{}] replace old observer[{}.{}] = [{}]", observer.getClass().getName(), method.getName(), o.getClass().getName(), m.getName(), listeners.remove(m, o));
            });
//            }
            listeners.putIfAbsent(method, observer);
            log.debug("注册事件消费者:subscriber=" + method + "observer=" + observer + ",event=" + eventClazz);
        }
    }

    /**
     * 同步发布事件,请尽量使用这个
     *
     * @param event 事件源对象
     */
    public <E extends AbstractEvent> void syncPublish(E event) {
        Map<Method, AbstractEventObserver> listeners = events.get(event.getClass());
        if (CollectionUtils.isEmpty(listeners)) {
            log.info("事件{}没有观察者", event);
            return;
        }
        long begin = System.currentTimeMillis();
        for (Entry<Method, AbstractEventObserver> eventEntry : listeners.entrySet()) {
            Method method = eventEntry.getKey();
            AbstractEventObserver observer = eventEntry.getValue();
            long s = System.currentTimeMillis();
            try {
                method.invoke(observer, event);
            } catch (Exception e) {
                log.error(e.getMessage() + ":" + event, e);
            } finally {
                long e = System.currentTimeMillis();
                long u = e - s;
                if (u > ((ActorThreadPoolExecutor) executor).slowly) {
                    log.warn("SLOWLY: {} do {}, used {} ms", method.toGenericString(), event, u);
                }
            }
        }
        long end = System.currentTimeMillis();
        long timeUsed = end - begin;
        if (timeUsed > ((ActorThreadPoolExecutor) executor).slowly) {
            log.warn("SLOWLY: rec {}, used {} ms,observers={}", event, timeUsed, listeners.size());
        }
        EventObjectPool.collectEventObj(event);
    }

    /**
     * 异步发布事件,相同事件串行发布
     *
     * @param event 事件源对象
     */
    public <E extends AbstractEvent> void asyncPublish(E event) {
        if (!events.containsKey(event.getClass())) {
            log.error("事件：{} 不存在", event.getClass());
            return;
        }
        executor.execute(new AbstractLinkedTask() {
            @Override
            public Object getIdentity() {
                return event.getClass();
            }

            @Override
            protected void exec() throws Exception {
                syncPublish(event);
            }
        });
    }

    /**
     * 指定线程异步发布事件
     *
     * @param threadId 决定时间消费所在的线程
     * @param event    事件源对象
     */
    public <E extends AbstractEvent> void asyncPublish(Object threadId, E event) {
        if (!events.containsKey(event.getClass())) {
            return;
        }
        executor.execute(new AbstractLinkedTask() {
            @Override
            public Object getIdentity() {
                return threadId;
            }

            @Override
            protected void exec() throws Exception {
                syncPublish(event);
            }
        });
    }
}
