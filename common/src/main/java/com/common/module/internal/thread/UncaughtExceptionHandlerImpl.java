
package com.common.module.internal.thread;

import org.slf4j.LoggerFactory;

import java.lang.Thread.UncaughtExceptionHandler;

public class UncaughtExceptionHandlerImpl implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		LoggerFactory.getLogger(getClass()).error(t.getName(), e);
	}
}
