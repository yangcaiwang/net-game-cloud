
package com.common.module.internal.loader.service;

import com.common.module.cluster.enums.ServerType;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.IRepository;
import com.common.module.internal.db.entity.IdentityCreator;
import com.common.module.internal.db.entity.Repositories;
import com.common.module.internal.delay.ServerTimerTask;
import com.common.module.internal.delay.ServerTimerTaskQueue;
import com.common.module.internal.thread.pool.scheduled.ScheduledExecutorServiceScheduler;
import com.common.module.internal.thread.task.AbstractCoreTask;

import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface IService {

	/**
	 * 配置的服務器类型-字符串
	 * 
	 * @return ServerType
	 */
	default ServerType getServerTypeName() {
		return IdentityCreator.SERVER_TYPE;
	}

	/**
	 * 服务器唯一ID
	 * @return String
	 */
	default String getServerKeyId() {
		return IdentityCreator.SERVER_TYPE.getServerId();
	}

	/**
	 * 获取MYSQL数据仓库
	 * 
	 * @param entityType
	 *            实体类型
	 */
	default <E extends DBEntity, R extends IRepository<E>> R getRepository(Class<E> entityType) {
		return Repositories.getRepository(entityType);
	}

	/**
	 * 获取service
	 * 
	 * @param clazz
	 *            接口 实体类型
	 */
	default <S extends AbstractService> S getService(Class<?> clazz) {
		S service;
		try {
			service = ServiceContext.getInstance().get(clazz);
			if (service == null)
				throw new RuntimeException(String.format("无法获取service,interface=[%s]", clazz));
			if (!(service instanceof AbstractService))
				throw new RuntimeException(String.format("[%s]并不是service,interface=[%s]", service, clazz));
			return service;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 获取service
	 * 
	 * @param name
	 *            接口simpleName
	 */
	default <S extends AbstractService> S getService(String name) {
		S service;
		try {
			service = ServiceContext.getInstance().get(name);
			if (service == null)
				throw new RuntimeException(String.format("无法获取service,name=[%s]", name));
			if (!(service instanceof AbstractService))
				throw new RuntimeException(String.format("[%s]并不是service,name=[%s]", service, name));
			return service;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 添加延迟任务
	 * 
	 * @param task 任务
	 * @return ServerTimerTask
	 */
	default ServerTimerTask addDelayTask(ServerTimerTask task) {
		return ServerTimerTaskQueue.getInstance().addDelayTask(task);
	}

	/**
	 * 添加延迟任务,只执行一次
	 * 
	 * @param id
	 *            唯一标识
	 * @param delay
	 *            延迟时间,如果max>1那么每次延迟时间都是这个数最小时间单位是10毫秒
	 * @param unit
	 *            时间单位
	 * @param param
	 *            携带的参数
	 * @param action
	 *            到点执行的拉姆达表达式
	 */
	default <Param extends Serializable> ServerTimerTask<Param> addDelayTask(String id, long delay, TimeUnit unit, Param param, Consumer<Param> action) {
		return addDelayTask(ServerTimerTask.valueOf(id, System.currentTimeMillis() + unit.toMillis(delay), param, action, 1));
	}

	/**
	 * 添加延迟任务,只执行一次
	 * 
	 * @param id
	 *            唯一标识
	 * @param delay
	 *            延迟时间,如果max>1那么每次延迟时间都是这个数,最小时间单位是10毫秒
	 * @param unit
	 *            时间单位
	 * @param action
	 *            到点执行的拉姆达表达式
	 * @return ServerTimerTask
	 */
	default ServerTimerTask addDelayTask(String id, long delay, TimeUnit unit, Consumer action) {
		return addDelayTask(ServerTimerTask.valueOf(id, System.currentTimeMillis() + unit.toMillis(delay), action, 1));
	}

	/**
	 * 添加延迟任务,只执行一次
	 * 
	 * @param delay
	 *            延迟时间,最小时间单位是10毫秒
	 * @param unit
	 *            时间单位
	 * @param action
	 *            到点执行的拉姆达表达式
	 * @return ServerTimerTask
	 */
	default ServerTimerTask addDelayTask(long delay, TimeUnit unit, Consumer action) {
		return addDelayTask(ServerTimerTask.valueOf(System.currentTimeMillis() + unit.toMillis(delay), action, 1));
	}

	/**
	 * 移除延迟任务
	 * 
	 * @param id
	 * @return ServerTimerTask
	 */
	default ServerTimerTask removeDelayTask(String id) {
		return ServerTimerTaskQueue.getInstance().removeDelayTask(id);
	}

	/**
	 * 异步执行任务,并且不等待回调,PS:这里执行任务的线程是不确定的,如果该任务需要写数据的操作,要考虑多线程问题,谨慎使用
	 * 
	 * @param r
	 *            只有run函数的任务(run->exec)
	 */
	default <R extends AbstractCoreTask> void runAsync(R r) {
		runAsync(r, ScheduledExecutorServiceScheduler.scheduler());
	}

	/**
	 * 异步执行任务,并且不等待回调,PS:这里执行任务的线程是不确定的,如果该任务需要写数据的操作,要考虑多线程问题,谨慎使用
	 * 
	 * @param r
	 *            只有run函数的任务(run->exec)
	 */
	default <R extends AbstractCoreTask> void runAsync(R r, Executor executor) {
		ScheduledExecutorServiceScheduler.execute(r, executor);
	}
}
