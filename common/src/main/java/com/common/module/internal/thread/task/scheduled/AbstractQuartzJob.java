package com.common.module.internal.thread.task.scheduled;

import com.common.module.internal.thread.task.ICoreTask;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 抽象的quartz时效任务
 */
public abstract class AbstractQuartzJob implements Job, ICoreTask {

	protected Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		long begin = System.currentTimeMillis();
		try {
			JobDataMap _jobDataMap = context.getJobDetail().getJobDataMap();
			String _jobName = (String) _jobDataMap.get("job.name");
			Class<?> _jobClazz = (Class<?>) _jobDataMap.get("job.class");
			if (_jobClazz != getClass()) {
				throw new RuntimeException(String.format("quartz.job[%s],class cast error,[%s]!=[%s]", _jobName, _jobClazz, getClass()));
			}
			this.exec(context);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			long timeUsed = System.currentTimeMillis() - begin;
			if (timeUsed > SLOWLY)
				log.warn("SLOWLY: {}, used {} ms", this, timeUsed);
		}
	}

	/**
	 * 执行任务
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected abstract void exec(JobExecutionContext context) throws Exception;

}
