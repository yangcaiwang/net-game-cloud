
package com.ycw.core.internal.loader.service;

import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.internal.script.Scripts;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <业务接口上下文类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ServiceContext {
    private static ConcurrentHashMap<String, AbstractService> serviceMap = new ConcurrentHashMap<>();
    private static final ServiceContext serviceContext = new ServiceContext();

    public static ServiceContext getInstance() {
        return serviceContext;
    }

    public <T extends AbstractService> T get(Class<?> clz) {
        return get(clz.getSimpleName());
    }

    public <T extends AbstractService> T get(String name) {
        return (T) serviceMap.get(name);
    }

    void put(Class<?> sur, AbstractService service) {
        if (Scripts.isScript(service)) {
            EventBusesImpl.getInstance().syncPublish(new ServiceModifiedEvent(service));
            return;
        }

        serviceMap.put(sur.getSimpleName(), service);
    }
}
