
package com.ycw.core.internal.loader.service;

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

    public AbstractService() {
        ServiceContext.getInstance().put(getClass(), this);
    }
}
