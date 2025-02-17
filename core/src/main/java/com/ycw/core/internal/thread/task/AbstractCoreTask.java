
package com.ycw.core.internal.thread.task;

import com.ycw.core.cluster.property.PropertyConfig;
import org.slf4j.Logger;

/**
 * <线程任务的抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractCoreTask implements ICoreTask, Runnable {

	transient protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	/**
	 * 慢任务时间-毫秒
	 */
	public int getSlowly() {
		return SLOWLY;
	}

	@Override
	public void run() {
		long begin = System.currentTimeMillis();
		try {
			exec();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (PropertyConfig.isDebug()) {
				long timeUsed = System.currentTimeMillis() - begin;
				if (timeUsed > getSlowly())
					log.warn("SLOWLY: {}, used {} ms", this, timeUsed);
			}
		}
	}

	protected abstract void exec() throws Exception;

}
