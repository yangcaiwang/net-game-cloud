
package com.common.module.internal.loader.service;

import com.common.module.util.CollectionUtils;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractService implements IService {

    transient protected final Logger log = LoggerFactory.getLogger(getClass());

    protected final Class<?>[] interfaces;

    public AbstractService() {
        interfaces = getClass().getInterfaces();
        if (!CollectionUtils.isEmpty(interfaces)) {
            for (Class<?> sur : interfaces) {
                if (sur != GroovyObject.class) {
                    ServiceContext.getInstance().addService(sur, this);
                }
            }
        }
    }
}
