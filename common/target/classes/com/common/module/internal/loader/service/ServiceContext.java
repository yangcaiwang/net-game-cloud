
package com.common.module.internal.loader.service;

import com.common.module.internal.event.EventBusesImpl;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.script.Scripts;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServiceContext {

    private static final Logger log = LoggerFactory.getLogger(ServiceContext.class);
    private static final ServiceContext serviceContext = new ServiceContext();
    private static Map<String, AbstractService> serviceMap = Maps.newConcurrentMap();

    public static ServiceContext getInstance() {
        return serviceContext;
    }

    public <T extends AbstractService> T get(Class<?> clz) {

        return get(clz.getSimpleName());
    }

    public <T extends AbstractService> T get(String name) {

        return (T) serviceMap.get(name);
    }

    void addService(Class<?> sur, AbstractService service) {
        if (Scripts.isScript(service)) {
            serviceMap.put(sur.getSimpleName(), service);
            log.info("add service [{}] of script [{}]", service, sur);
            EventBusesImpl.getInstance().syncPublish(new ServiceModifiedEvent(service));
            return;
        }
        if (serviceMap.containsKey(sur.getSimpleName())) {
            return;
        }
        serviceMap.put(sur.getSimpleName(), service);
        if (PropertyConfig.isDebug()) {
            log.info("add service [{}] of java [{}]", service, sur);
        }
    }
}
