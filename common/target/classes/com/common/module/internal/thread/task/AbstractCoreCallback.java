
package com.common.module.internal.thread.task;

import org.slf4j.Logger;

import java.util.concurrent.Callable;

/**
 * 抽象的带回调的任务
 *
 * @param <V>
 */
public abstract class AbstractCoreCallback<V> implements ICoreTask, Callable<V> {

	transient protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	public int getSlowly() {
		return SLOWLY;
	}

	@Override
	public V call() {

		long begin = System.currentTimeMillis();
		try {
			return callback();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			long timeUsed = System.currentTimeMillis() - begin;
			if (timeUsed > getSlowly())
				log.warn("SLOWLY: {}, used {} ms", this, timeUsed);
		}
	}

	protected abstract V callback() throws Exception;

}