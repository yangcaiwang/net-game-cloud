
package com.common.module.internal.thread;

import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * <线程抛出异常处理器实现类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class UncaughtExceptionHandlerImpl implements UncaughtExceptionHandler {

    @Override
    public void uncaughtException(Thread t, Throwable e) {

        LoggerFactory.getLogger(getClass()).error(t.getName(), e);
    }
}
