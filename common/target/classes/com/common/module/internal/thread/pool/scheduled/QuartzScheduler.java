package com.common.module.internal.thread.pool.scheduled;

import com.common.module.internal.thread.task.scheduled.AbstractQuartzJob;
import com.common.module.util.DateUnit;
import com.common.module.util.StringUtils;
import com.google.common.collect.Maps;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.OperableTrigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 时效任务封装,任务名称用Job的class名称,每次启动任务时会先关闭同名的老任务,所以使用时如果同一个任务被多次调用是不允许的</br>
 * 支持cron表达式和时间管理,任务实体都继承自Job,切忌一点,Job实现类不能有带参数构造器,内部是通过反射创建的实例,
 * 需要用到的参数通过jobDataMap传入 如果需定制配置,请创建my_quartz.properties文件在服务根目录
 */
public class QuartzScheduler {

	private static final Logger log = LoggerFactory.getLogger(QuartzScheduler.class);

	private static final String job_group = "Default-Quartz-Group";

	private static final String trigger_group = job_group;

	private static Scheduler scheduler;

	public static Scheduler scheduler() {
		if (scheduler == null) {
			synchronized (QuartzScheduler.class) {
				if (scheduler == null) {
					try {
						scheduler = new StdSchedulerFactory().getScheduler();
						scheduler.standby();
						scheduler.start();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			}
		}
		return scheduler;
	}

	public static boolean isShutdown() {
		try {
			return scheduler().isShutdown();
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 延迟delay毫秒执行,请使用@see {@link ScheduledExecutorServiceScheduler}
	 * 
	 * @param name
	 * @param job
	 * @param delay
	 */
	public static void schedule(String name, Class<? extends AbstractQuartzJob> job, long delay) {
		schedule(name, job, new Date(System.currentTimeMillis() + delay), 0, 0);
	}

	/**
	 * initialDelay毫秒后每隔period毫秒重复执行,请使用@see
	 * {@link ScheduledExecutorServiceScheduler}
	 * 
	 * @param name
	 * @param job
	 * @param initialDelay
	 * @param period
	 */
	public static void schedule(String name, Class<? extends AbstractQuartzJob> job, long initialDelay, long period) {
		schedule(name, job, new Date(System.currentTimeMillis() + initialDelay), period, -1);
	}

	/**
	 * 延迟delay毫秒执行,请使用@see {@link ScheduledExecutorServiceScheduler}
	 * 
	 * @param name
	 * @param job
	 * @param delay
	 * @param jobDataMap
	 *            执行任务需要携带的参数
	 */
	public static void schedule(String name, Class<? extends AbstractQuartzJob> job, long delay, Map<String, Object> jobDataMap) {
		schedule(name, job, new Date(System.currentTimeMillis() + delay), 0, 0, jobDataMap);
	}

	/**
	 * initialDelay毫秒后每隔period毫秒重复执行,请使用@see
	 * {@link ScheduledExecutorServiceScheduler}
	 * 
	 * @param name
	 * @param job
	 * @param initialDelay
	 * @param period
	 * @param jobDataMap
	 *            执行任务需要携带的参数,如果在执行时需要更新参数内容,需要把当前的任务关闭重新启动新任务,组装新的参数
	 */
	public static void schedule(String name, Class<? extends AbstractQuartzJob> job, long initialDelay, long period, Map<String, Object> jobDataMap) {
		schedule(name, job, new Date(System.currentTimeMillis() + initialDelay), period, -1, jobDataMap);
	}

	/**
	 * 根据cron表达式解析任务执行时间
	 * 
	 * @param name
	 * @param cron
	 * @param job
	 */
	public static void schedule(String name, String cron, Class<? extends AbstractQuartzJob> job) {
		schedule(name, cron, job, null);
	}

	/**
	 * 根据cron表达式解析任务执行时间
	 * 
	 * @param name
	 * @param cron
	 * @param job
	 * @param jobDataMap
	 *            执行任务需要携带的参数,如果在执行时需要更新参数内容,需要把当前的任务关闭重新启动新任务,组装新的参数
	 */
	public static void schedule(String name, String cron, Class<? extends AbstractQuartzJob> job, Map<String, Object> jobDataMap) {
		checkJob(job);
		String jobName = generatedJobName(name, job, JobState.ADD);
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setGroup(job_group);
		jobDetail.setName(jobName);
		jobDetail.setJobClass(job);
		if (jobDataMap == null)
			jobDataMap = Maps.newHashMap();
		jobDataMap.put("job.class", job);
		jobDataMap.put("job.name", jobName);
		jobDetail.setJobDataMap(new JobDataMap(jobDataMap));
		CronTriggerImpl trigger = new CronTriggerImpl();
		trigger.setGroup(trigger_group);
		trigger.setName(jobName);
		try {
			trigger.setCronExpression(cron);
			scheduler().scheduleJob(jobDetail, trigger);
			log.info("schedule:jobName=[{}],cron=[{}],job=[{}],params=[{}]", jobName, cron, job, jobDataMap);
		} catch (Exception e) {
			throw new RuntimeException(cron + "," + job + "," + jobDataMap, e);
		}
	}

	/**
	 * 按约定执行任务,请使用@see {@link ScheduledExecutorServiceScheduler}
	 * 
	 * @param name
	 *            任务名
	 * @param job
	 *            任务执行实体
	 * @param startTime
	 *            开始执行时间
	 * @param repeatInterval
	 *            下一次执行任务的时间间隔-毫秒
	 * @param repeatCount
	 *            重复次数
	 */
	public static void schedule(String name, Class<? extends AbstractQuartzJob> job, Date startTime, long repeatInterval, int repeatCount) {
		schedule(name, job, startTime, repeatInterval, repeatCount, null);
	}

	/**
	 * 按约定执行任务,请使用@see {@link ScheduledExecutorServiceScheduler}
	 * 
	 * @param name
	 *            任务名
	 * @param job
	 *            任务执行实体
	 * @param startTime
	 *            开始执行时间
	 * @param repeatInterval
	 *            下一次执行任务的时间间隔-毫秒
	 * @param repeatCount
	 *            重复次数,-1表示无限次
	 * @param jobDataMap
	 *            执行任务需要携带的参数,如果在执行时需要更新参数内容,需要把当前的任务关闭重新启动新任务,组装新的参数
	 */
	public static void schedule(String name, Class<? extends AbstractQuartzJob> job, Date startTime, long repeatInterval, int repeatCount, Map<String, Object> jobDataMap) {
		checkJob(job);
		String jobName = generatedJobName(name, job, JobState.ADD);
		JobDetailImpl jobDetail = new JobDetailImpl();
		jobDetail.setGroup(job_group);
		jobDetail.setName(jobName);
		jobDetail.setJobClass(job);
		if (jobDataMap == null)
			jobDataMap = Maps.newHashMap();
		jobDataMap.put("job.class", job);
		jobDataMap.put("job.name", jobName);
		jobDetail.setJobDataMap(new JobDataMap(jobDataMap));
		SimpleTriggerImpl trigger = new SimpleTriggerImpl();
		trigger.setGroup(trigger_group);
		trigger.setName(jobName);
		trigger.setStartTime(startTime);
		trigger.setRepeatInterval(repeatInterval);
		trigger.setRepeatCount(repeatCount);
		try {
			scheduler().scheduleJob(jobDetail, trigger);
			log.info("schedule:jobName=[{}],start=[{}],interval=[{}],count=[{}],params=[{}]", jobName, DateUnit.timeFormat(DateUnit.DATE_FORMAT_SSS, startTime.getTime()), repeatInterval, repeatCount, jobDataMap);
		} catch (Exception e) {
			throw new RuntimeException(job + "," + DateUnit.timeFormat(DateUnit.DATE_FORMAT_SSS, startTime.getTime()) + "," + repeatInterval + "," + repeatCount + "," + jobDataMap, e);
		}
	}

	/**
	 * 修改cron任务
	 * 
	 * @param name
	 * @param job
	 * @param newCron
	 */
	public static void modifyJob(String name, Class<? extends AbstractQuartzJob> job, String newCron) {
		try {
			String jobName = generatedJobName(name, job, JobState.MODIFY);
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, trigger_group);
			Scheduler scheduler = scheduler();
			CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			if (trigger != null) {
				String oldCron = trigger.getCronExpression();
				if (!oldCron.equalsIgnoreCase(newCron)) {
					// 触发器
					TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
					// 触发器名,触发器组
					triggerBuilder.withIdentity(jobName, trigger_group);
					triggerBuilder.startNow();
					// 触发器时间设定
					triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(newCron));
					// 创建Trigger对象
					trigger = (CronTrigger) triggerBuilder.build();
					// 方式一 ：修改一个任务的触发时间
					scheduler.rescheduleJob(triggerKey, trigger);
					log.info("modifyJob:[{}],[{}]->[{}]", jobName, oldCron, newCron);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(newCron + "," + job, e);
		}
	}

	/**
	 * 取消一批任务
	 */
	public static void unschedule(Map<String, Class<? extends AbstractQuartzJob>> m) {
		m.forEach((name, job) -> {
			try {
				unschedule(name, job);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		});
	}

	/**
	 * 取消任务
	 * 
	 * @param name
	 * @param job
	 */
	public static boolean unschedule(String name, Class<? extends AbstractQuartzJob> job) {
		try {
			Scheduler scheduler = scheduler();
			if (!scheduler.isStarted()) {
				return false;
			}
			String jobName = generatedJobName(name, job, JobState.REMOVE);
			TriggerKey triggerKey = TriggerKey.triggerKey(jobName, trigger_group);
			scheduler.pauseTrigger(triggerKey);
			scheduler.unscheduleJob(triggerKey);
			JobKey jobKey = JobKey.jobKey(jobName, job_group);
			boolean deleted = scheduler.deleteJob(jobKey);
			log.info("unschedule:[{}]{}", jobName, deleted);
			return deleted;
		} catch (Exception e) {
			throw new RuntimeException(job + ":" + e.getMessage(), e);
		} finally {
		}
	}

	/**
	 * 任务checker
	 * 
	 * @param job
	 * @return
	 */
	private static void checkJob(Class<? extends AbstractQuartzJob> job) {
		if (!Modifier.isPublic(job.getModifiers())) {
			throw new RuntimeException(String.format("任务[%s]必须是public修饰!", job));
		}
		Constructor<?>[] constructors = job.getDeclaredConstructors();
		if (constructors.length > 1) {
			throw new RuntimeException(String.format("任务[%s]只能有一个构造器!", job));
		}
		Constructor<?> constructor = constructors[0];
		if (!Modifier.isPublic(constructor.getModifiers())) {
			throw new RuntimeException(String.format("任务[%s]构造器[%s]必须是public修饰!", job, constructor));
		}
		Class<?>[] types = constructor.getParameterTypes();
		if (types != null && types.length > 0) {
			throw new RuntimeException(String.format("任务[%s]构造器[%s]不允许有参数!", job, constructor));
		}
	}

	/**
	 * 任务名
	 * 
	 * @param name
	 * @param job
	 * @return
	 * @throws Exception
	 */
	private static String generatedJobName(String name, Class<? extends AbstractQuartzJob> job, JobState state) {
		String jobName = StringUtils.merged(job.getSimpleName(), name);
		if (StringUtils.stringContainIt(jobName, StringUtils.Type.TYPE_CHINESE)) {
			throw new RuntimeException(String.format("job名称[%s]不要设置中文啊!!!", jobName));
		}
		if (state == JobState.ADD) {
		} else if (state == JobState.REMOVE) {
		} else if (state == JobState.MODIFY) {
		}
		return jobName;
	}

	public static void shutdown() {
		try {
			scheduler().shutdown(true);
		} catch (Exception e) {
			throw new RuntimeException("shutdown!", e);
		}
	}

	public static void shutdownNow() {
		try {
			scheduler().shutdown(false);
		} catch (Exception e) {
			throw new RuntimeException("shutdownNow!", e);
		}
	}

	private static enum JobState {
		ADD, REMOVE, MODIFY,
	}

	public static void main(String[] args) {

		String cron = "0 0 0 * * ?";
		Date[] nexts = Parser.getNextTriggerTimes(cron, 3);
		for (int i = 0; i < nexts.length; i++) {
			Date date = nexts[i];
			System.err.println(DateUnit.timeFormat(date.getTime()));
		}
		System.err.println("###################################");
		List<Date> dates = Parser.computeFireTimesBetween(cron, new Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)), new Date());
		dates.forEach(date -> {
			System.err.println(DateUnit.timeFormat(date.getTime()));
		});
		System.err.println("******************************************************");
		Date end = Parser.computeEndTimeToAllowParticularNumberOfFirings(cron, 3);
		System.err.println(DateUnit.timeFormat(end.getTime()));
	}

	/**
	 * 解析cron表达式计算最近执行的时间
	 * 
	 * @author lijishun
	 */
	public static final class Parser {

		/**
		 * 获取下次执行时间
		 * 
		 * @param cron
		 *            表达式
		 * @return
		 */
		public static Date getNextTriggerTime(String cron) {

			return getNextTriggerTimes(cron, 1)[0];
		}

		/**
		 * 获取下N次执行时间
		 * 
		 * @param cron
		 *            表达式
		 * @param count
		 *            次数
		 * @return
		 */
		public static Date[] getNextTriggerTimes(String cron, int count) {

			return computeFireTimes(cron, count).toArray(new Date[count]);
			// try {
			// new CronExpression(cron);
			// } catch (ParseException e) {
			// throw new RuntimeException(cron, e);
			// }
			// Date[] result = new Date[count];
			// Date time0 = null;
			// Date nextTime = null;
			// CronTrigger trigger =
			// TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			// for (int i = 0; i < count; i++) {
			// if (time0 == null) {
			// time0 = trigger.getStartTime();
			// } else {
			// time0 = nextTime;
			// }
			// nextTime = trigger.getFireTimeAfter(time0);
			// result[i] = nextTime;
			// }
			// return result;
		}

		/**
		 * 下N次任务执行时间点
		 * 
		 * @param cron
		 *            表达式
		 * @param count
		 *            下N次
		 * @return
		 */
		public static List<Date> computeFireTimes(String cron, int count) {

			CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			return TriggerUtils.computeFireTimes((OperableTrigger) trigger, null, count);
		}

		/**
		 * 根据表达式计算两个时间之间所有的执行时间点
		 * 
		 * @param cron
		 *            表达式
		 * @param from
		 *            开始时间
		 * @param to
		 *            结束时间
		 * @return 所有任务执行点
		 */
		public static List<Date> computeFireTimesBetween(String cron, Date from, Date to) {

			CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			return TriggerUtils.computeFireTimesBetween((OperableTrigger) trigger, null, from, to);
		}

		/**
		 * 指定N次后任务结束时间
		 * 
		 * @param cron
		 *            表达式
		 * @param count
		 *            预期执行次数
		 * @return
		 */
		public static Date computeEndTimeToAllowParticularNumberOfFirings(String cron, int count) {

			CronTrigger trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
			return TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings((OperableTrigger) trigger, null, count);
		}
	}

}
