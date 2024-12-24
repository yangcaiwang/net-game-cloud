
package com.common.module.internal.thread.task;

import com.common.module.cluster.property.PropertyConfig;
import org.slf4j.Logger;

/**
 * 抽象任务
 */
public abstract class AbstractCoreTask implements ICoreTask, Runnable {

	transient protected final Logger log = org.slf4j.LoggerFactory.getLogger(getClass());

	/**
	 * 慢任务时间-毫秒
	 * 
	 * @return
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
