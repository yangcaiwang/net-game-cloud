
package com.common.module.internal.db.entity;

import com.common.module.internal.thread.NamedThreadFactory;
import com.common.module.internal.thread.pool.actor.ActorThreadPoolExecutor;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <缓存数据池类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class Repositories {

	static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("db-scheduler"));
	static final ActorThreadPoolExecutor actorThreadPoolExecutor = new ActorThreadPoolExecutor("db-saver", Runtime.getRuntime().availableProcessors());

	/**
	 * 所有的数据仓库
	 */
	public static final Map<Class<? extends DBEntity>, IRepository<? extends DBEntity>> repositories = Maps.newConcurrentMap();

	static <E extends DBEntity, Repository extends IRepository<E>> void addRepository(Class<E> entityType, Repository repository) {
		Repository oldRepository = ((Repository) repositories.put(entityType, repository));
		if (oldRepository != null)
			throw new RuntimeException(String.format("数据仓库[{}],[{}]配置了相同的实体类型[{}]", repository, oldRepository, entityType));
	}

	public static Collection<IRepository<? extends DBEntity>> getAllRepository() {
		return repositories.values();
	}

	/**
	 * 获取数据仓库
	 */
	public static <E extends DBEntity, Repository extends IRepository<E>> Repository getRepository(Class<E> entityType) {
		Objects.requireNonNull(entityType);
		Repository repository = ((Repository) repositories.get(entityType));
		Validate.isTrue(repository != null, "请检查【%s】配置的注解是否和db配置一致", entityType);
		return repository;
	}

	/**
	 * 销毁保存数据定时任务并一次性异步把全部缓存的数据全部保存到db
	 * 
	 */
	public static void flushAll() {
		scheduledExecutorService.shutdown();
		while (!scheduledExecutorService.isTerminated()) {
			try {
				scheduledExecutorService.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				break;
			}
		}
		List<IRepository<? extends DBEntity>> list = repositories.values().stream().filter(r -> r instanceof DelayCachedRepository).collect(Collectors.toList());
		repositories.values().forEach(IRepository::serverClose);
		list.forEach(r -> ((DelayCachedRepository<? extends DBEntity>) r).shutDownFlushAll());
		list.forEach(r -> ((DelayCachedRepository<? extends DBEntity>) r).flushAll());
		actorThreadPoolExecutor.shutdownFully();
	}

	///////////////////////////////////////////////// Caffeine - Cache - Options
	///////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////////////////

	static public <T extends DBEntity> T get(Class<T> entityType, Serializable... pks) {
		IRepository<T> repository = getRepository(entityType);
		if (repository == null) {
			return null;
		}
		return repository.get(pks);
	}

	static public <T extends DBEntity> Map<String, T> getAll(Class<T> entityType, Iterable<String> keys) {

		return (getRepository(entityType)).getAll(keys);
	}

	static public <T extends DBEntity> Map<String, T> getAll(Class<T> entityType, Serializable[]... pksArray) {

		return (getRepository(entityType)).getAll(pksArray);
	}

	static public <T extends DBEntity> Map<String, T> getAll(Class<T> entityType, List<Serializable[]> pksList) {

		return (getRepository(entityType)).getAll(pksList);
	}

	static public <T extends DBEntity> Map<String, T> getAll(Class<T> entityType, Collection<Long> keys) {

		return (getRepository(entityType)).getAll(keys);
	}

	static public <T extends DBEntity> T makeSure(Class<T> entityType) {

		return (getRepository(entityType)).makeSure();
	}

	static public <T extends DBEntity> T makeSure(Class<T> entityType, Predicate<T> predicate) {

		return (getRepository(entityType)).makeSure(predicate);
	}

	static public <T extends DBEntity> T makeSure(Class<T> entityType, Serializable... pks) {

		return (getRepository(entityType)).makeSure(pks);
	}

	static public <T extends DBEntity> T makeSure(Class<T> entityType, Predicate<T> predicate, Serializable... pks) {

		return (getRepository(entityType)).makeSure(predicate, pks);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Predicate<T> predicate, Iterable<String> keys) {

		return (getRepository(entityType)).makeSureAll(predicate, keys);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Iterable<String> keys) {

		return (getRepository(entityType)).makeSureAll(keys);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Predicate<T> predicate, Collection<Long> keys) {

		return (getRepository(entityType)).makeSureAll(predicate, keys);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Collection<Long> keys) {

		return (getRepository(entityType)).makeSureAll(keys);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Predicate<T> predicate, int num) {

		return (getRepository(entityType)).makeSureAll(predicate, num);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, int num) {

		return (getRepository(entityType)).makeSureAll(num);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Serializable[]... pksArray) {
		return (getRepository(entityType)).makeSureAll(pksArray);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, List<Serializable[]> pksList) {
		return (getRepository(entityType)).makeSureAll(pksList);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Predicate<T> predicate, Serializable[]... pksArray) {
		return (getRepository(entityType)).makeSureAll(predicate, pksArray);
	}

	static public <T extends DBEntity> Map<String, T> makeSureAll(Class<T> entityType, Predicate<T> predicate, List<Serializable[]> pksList) {
		return (getRepository(entityType)).makeSureAll(predicate, pksList);
	}

	///////////////////////////////////////////////// Caffeine - Cache - Options
	///////////////////////////////////////////////// ////////////////////////////////////////////////////////////////////////////////
}
