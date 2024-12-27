
package com.ycw.core.internal.loader.service;

import com.ycw.core.util.CollectionUtils;
import groovy.lang.GroovyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <业务接口抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
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
