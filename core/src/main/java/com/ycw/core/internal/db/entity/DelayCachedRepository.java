
package com.ycw.core.internal.db.entity;

import com.github.benmanes.caffeine.cache.*;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.internal.db.annotation.Cached;
import com.ycw.core.internal.random.ProbabilityUtils;
import com.ycw.core.util.ArraysUtils;
import com.ycw.core.util.BatchUtils;
import com.ycw.core.util.CollectionUtils;
import com.ycw.core.util.StringUtils;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <延时入库仓库抽象类>
 * <p>
 * ps: 咖啡因缓存延时入库数据仓库
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
abstract public class DelayCachedRepository<E extends DBEntity> extends JdbcRepository<E> {

    LoadingCache<String, E> localCache;
    Set<E> delayCache;
    Cached cachedConfig;
    Map<String, E> delEntityCache;
    // 清表
    volatile boolean clearAll;

    public DelayCachedRepository() {
        super();

        cachedConfig = DBEntityUtils.cache(entityType);
        Validate.isTrue(cachedConfig.liveDuration() == Cached.NEVER_EXPIRE || cachedConfig.liveDuration() > 0);
        Validate.isTrue(cachedConfig.liveDuration() == Cached.NEVER_EXPIRE || cachedConfig.liveDuration() > cachedConfig.delay() * 2);

        delayCache = Sets.newConcurrentHashSet();

        if (!isLogger) {
            @NonNull
            Caffeine<Object, Object> caffeine = Caffeine.newBuilder();
            caffeine.recordStats();
            caffeine.initialCapacity(cachedConfig.concurrentLevel());
            caffeine.maximumSize(cachedConfig.maximumSize());
            if (cachedConfig.liveDuration() != Cached.NEVER_EXPIRE) {
                caffeine.expireAfterAccess(cachedConfig.liveDuration(), TimeUnit.SECONDS);
                caffeine.refreshAfterWrite(cachedConfig.liveDuration(), TimeUnit.SECONDS);
            }
            caffeine.removalListener((@Nullable String key, @Nullable E value, @NonNull RemovalCause cause) -> {
                if (clearAll) {
                    return;
                }
                if (cachedConfig.readNotSave()) {
                    return;
                }
                if (cause == RemovalCause.EXPLICIT) {
                    return;
                }
                if (value != null) {
                    removeAndSave(value);
                }
            });
            delEntityCache = new ConcurrentHashMap<>();
            caffeine.writer(new CacheWriter<String, E>() {
                @Override
                public void write(@NonNull String key, @NonNull E value) {
                }

                @Override
                public void delete(@NonNull String key, @Nullable E value, @NonNull RemovalCause cause) {
                    if (RemovalCause.EXPIRED == cause) {
                        if (delEntityCache != null) {
                            delEntityCache.put(key, value);
                        }
                    }
                }
            });
            localCache = caffeine.build(new CacheLoader<String, E>() {
                @Nullable
                @Override
                public E load(@NonNull String key) throws Exception {
                    return null;
                }

                @Override
                public @Nullable E reload(@NonNull String key, @NonNull E oldValue) throws Exception {
                    return oldValue;
                }
            });

            if (cachedConfig.liveDuration() == Cached.NEVER_EXPIRE) {
                List<E> listAll = super.listAll();
                if (!CollectionUtils.isEmpty(listAll)) {
                    localCache.putAll(super.toMap(listAll));
                }
            }
        }
        if (cachedConfig.delay() > 0) {
            int initialDelay = ProbabilityUtils.random(cachedConfig.delay(), cachedConfig.delay() * 2);
            int period = ProbabilityUtils.random(cachedConfig.delay() / 2, cachedConfig.delay() * 2);
            Repositories.scheduledExecutorService.scheduleWithFixedDelay(this::asyncSaving, Math.max(2, initialDelay), Math.max(2, period), TimeUnit.SECONDS);
        }
        log.debug("DelayCached数据仓库创建成功:\nentityType[{}],\ncached[{}],\n", entityType, cachedConfig);
    }

    void asyncSaving() {
        asyncExecute(this::flushing);
    }

    void asyncExecute(Runnable runnable) {
        Repositories.actorThreadPoolExecutor.addLast(entityType, runnable);
    }

    /**
     * 清空所有缓存和table的数据,并且截断数据表,!!!必须确保缓存数据已经全部落地!!!
     */
    @Override
    public void clearAll() {
//		if (DBEntityUtils.needRoll(entityType) || tableNum > 1)
//			throw new RuntimeException("can not clearAll  : " + entityType);
        if (DBEntityUtils.needRoll(entityType)) {
            log.error("can not clearAll  : " + entityType);
            return;
        }
        clearAll = true;
        if (localCache != null) {
            localCache.invalidateAll();
        }
        asyncExecute(this::asyncClearAll);
    }

    private void asyncClearAll() {
        if (localCache != null)
            localCache.invalidateAll();
        if (!CollectionUtils.isEmpty(delayCache))
            delayCache.clear();
        if (!CollectionUtils.isEmpty(delEntityCache)) {
            delEntityCache.clear();
        }
        log.info("清空表数据，{}", entityType);
        if (tableNum > 1) {
            super.clearMulAll();
        } else {
            super.clearAll();
        }
        clearAll = false;
    }

    private boolean removeAndSave(E value) {
        if (!Strings.isNullOrEmpty(value.getKey())) {
            if (delEntityCache != null && delEntityCache.containsKey(value.getKey())) {
                save(value);
                return true;
            }
            if (!asMap().containsKey(value.getKey())) {
                save(value);
            }
        }
        return false;
    }

    /**
     * 一次性把所有缓存发生变更的对象持久化到db 由定时器或者异步线程调用
     */
    protected void flushing() {
        if (CollectionUtils.isEmpty(delayCache)) {
            return;
        }
        Set<E> tmpCache = delayCache;
        delayCache = new CopyOnWriteArraySet<>();

        if (delEntityCache != null && !delEntityCache.isEmpty()) {
            tmpCache.forEach(v -> {
                if (!Strings.isNullOrEmpty(v.getKey())) {
                    delEntityCache.remove(v.getKey());
                }
            });
        }

        Map<MySQLable.Options, List<E>> optionsMap = tmpCache.stream().collect(Collectors.groupingBy(DBEntity::options, Collectors.toList()));
        Set<MySQLable.Options> treeSet = Sets.newTreeSet(optionsMap.keySet());// 排序,按枚举ordinal次序,切勿改动Options次序
        for (MySQLable.Options options : treeSet) {
            List<E> optList = optionsMap.get(options);
            if (optList.size() <= cachedConfig.limit()) {
                saving(options, optList);
            } else {
                List<List<E>> lists = BatchUtils.toLists(optList, cachedConfig.limit());
                for (List<E> list : lists) {
                    saving(options, list);
                }
            }
        }
    }

    private Set<E> mergeChangeCache() {
        Set<E> tmpCache = new CopyOnWriteArraySet<>();
        if (!isLogger) {
            Map<String, E> map = new HashMap<>();
            // 合并缓存key一样的记录，存在一条记录多个不同对象（如果失效也保存的情况下），优先取有操作的，忽略无操作的
            for (E e : delayCache) {
                if (!"0".equals(e.getKey()) && !"null".equals(e.getKey())) {
                    if (map.containsKey(e.getKey())) {
                        E e1 = map.get(e.getKey());
                        if (e1.options() != MySQLable.Options.NONE && e.options() == MySQLable.Options.NONE) {
                            log.info("实体：{} 保存队列存在相同key;{}", entityType, e.getKey());
                            continue;
                        }
                    }
                    map.put(e.getKey(), e);
                } else {
                    tmpCache.add(e);
                }
            }
            if (!map.isEmpty() && tmpCache.isEmpty()) {
                tmpCache.addAll(map.values());
                map.clear();
            }
        } else {
            tmpCache.addAll(delayCache);
        }
        return tmpCache;
    }

    void saving(MySQLable.Options options, Collection<E> c) {
        for (E o : c) {
            o.freedom();// 状态还原，这一步一定不能少
        }
        if (options.saving(entityType, c)) {
            if (PropertyConfig.isDebug()) {
                log.info("saved [{}],size=[{}],opt=[{}]", entityType, c.size(), options);
            }
            if (localCache != null && options == MySQLable.Options.DELETE) {
                Iterable<? extends String> deleteKeys = c.stream().collect(Collectors.toMap(o -> o.getKey(), v -> v)).keySet();
                localCache.invalidateAll(deleteKeys);
                if (delEntityCache != null) {
                    deleteKeys.forEach(v -> {
                        delEntityCache.remove(v);
                    });
                }
            }
        } else {
            if (options == MySQLable.Options.INSERT || options == MySQLable.Options.UPDATE) {
                log.error("saved [{}],size=[{}],opt=[{}] 保存失败", entityType, c.size(), options);
            }
        }
    }

    void shutDownFlushAll() {
        if (localCache != null) {
            localCache.cleanUp();
        }
    }

    public void flushAll() {
        Map<String, E> asMap = asMap();
        if (!CollectionUtils.isEmpty(asMap)) {
//			asMap.values().forEach(v -> {
//				if (v.options() == MySQLable.Options.NONE) {
//					v.setOptionsNoNotify(getRemoveOpt());
//				}
//			});
            addToDelayCache(asMap.values());
        }
        asyncSaving();
    }

    /**
     * 异步保存实体,并不一定会立刻执行
     */
    protected boolean save(E obj) {
        if (clearAll) {
            return false;
        }
        addToDelayCache(obj);
        if (cachedConfig.delay() == 0) {
            asyncSaving();
        }
        return true;
    }

    /**
     * 批量异步保存实体,并不一定会立刻执行
     */
    protected boolean save(Collection<E> c) {
        if (clearAll) {
            return false;
        }
        addToDelayCache(c);
        if (cachedConfig.delay() == 0) {
            asyncSaving();
        }
        return true;
    }

    void addToDelayCache(E obj) {
        delayCache.add(obj);
    }

    void addToDelayCache(Collection<E> c) {
        delayCache.addAll(c);
    }

    @Override
    public E makeSure() {
        return _create(null, makePk());
    }

    @Override
    public E makeSure(Predicate<E> predicate) {
        return _create(predicate, makePk());
    }

    @Override
    public E makeSure(Serializable... pks) {
        return _makeSure(null, pks);
    }

    @Override
    public E makeSure(Predicate<E> predicate, Serializable... pks) {
        return _makeSure(predicate, pks);
    }

    @Override
    public Map<String, E> makeSureAll(Predicate<E> predicate, Serializable[]... pksArray) {
        Set<String> keySet = Sets.newHashSet();
        for (Serializable[] pks : pksArray)
            keySet.add(merged2Key(pks));
        return _makeSureAll(keySet, predicate);
    }

    @Override
    public Map<String, E> makeSureAll(Serializable[]... pksArray) {
        return makeSureAll(null, pksArray);
    }

    @Override
    public Map<String, E> makeSureAll(Predicate<E> predicate, List<Serializable[]> pksList) {
        Set<String> keySet = Sets.newHashSet();
        pksList.forEach(pks -> keySet.add(merged2Key(pks)));
        return _makeSureAll(keySet, predicate);
    }

    @Override
    public Map<String, E> makeSureAll(List<Serializable[]> pksList) {
        return makeSureAll(null, pksList);
    }

    @Override
    public Map<String, E> makeSureAll(Predicate<E> predicate, Iterable<String> keys) {
        return _makeSureAll(keys, predicate);
    }

    @Override
    public Map<String, E> makeSureAll(Iterable<String> keys) {
        return makeSureAll(null, keys);
    }

    @Override
    public Map<String, E> makeSureAll(Predicate<E> predicate, Collection<Long> keys) {
        Set<String> keySet = Sets.newHashSet();
        keys.forEach(pk -> keySet.add(String.valueOf(pk)));
        return _makeSureAll(keySet, predicate);
    }

    @Override
    public Map<String, E> makeSureAll(Collection<Long> keys) {
        return makeSureAll(null, keys);
    }

    @Override
    public Map<String, E> makeSureAll(Predicate<E> predicate, int num) {
        List<String> keys = Lists.newArrayListWithCapacity(num);
        while (keys.size() < num)
            keys.add(String.valueOf(makePk()));
        return _createAll(keys, predicate);
    }

    @Override
    public Map<String, E> makeSureAll(int num) {
        return makeSureAll(null, num);
    }

    @Override
    public Boolean delete(Serializable... pks) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(pks));
        }
        E o = get(pks);
        if (o != null) {
            o.delete();
        }
        return true;
    }

    @Override
    public Boolean delete(E... objs) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(objs));
        }
        for (E e : objs)
            e.delete();
        return true;
    }

    @Override
    public boolean deleteNow(String... pks) {
        for (String pk : pks) {
            localCache.invalidate(pk);
            if (!CollectionUtils.isEmpty(delayCache))
                delayCache.removeIf(v -> Objects.equals(v.getKey(), pk));
            if (!CollectionUtils.isEmpty(delEntityCache)) {
                delEntityCache.remove(pk);
            }
        }

        super.deleteByKeys(pks);
        return true;
    }

    @Override
    public Boolean delete(Collection<E> c) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(c));
        }
        c.forEach(e -> e.delete());
        return true;
    }

    @Override
    public Boolean delete(String condition) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + condition);
        }
        return super.delete(condition);
    }

    /////////////////////////////////////////////////////// 重写父类的get方法/////////////////////////////////////////////////////////////////////
    @Override
    public E get(Serializable... pks) {
        E o = _get(merged2Key(pks));
        return o;
    }

    @Override
    public Map<String, E> getAll(Serializable[]... pksArray) {
        return getAll(null, pksArray);
    }

    public Map<String, E> getAll(Predicate<E> predicate, Serializable[]... pksArray) {
        Set<String> keySet = Sets.newHashSet();
        for (Serializable[] pks : pksArray) {
            keySet.add(merged2Key(pks));
        }
        Map<String, E> m = _getAll(predicate, keySet);
        return m;
    }

    @Override
    public Map<String, E> getAll(List<Serializable[]> pksList) {
        return getAll(null, pksList);
    }

    public Map<String, E> getAll(Predicate<E> predicate, List<Serializable[]> pksList) {
        Set<String> keySet = Sets.newHashSet();
        for (Serializable[] pks : pksList) {
            keySet.add(merged2Key(pks));
        }
        Map<String, E> m = _getAll(predicate, keySet);
        return m;
    }

    @Override
    public Map<String, E> getAll(Iterable<String> keys) {
        return getAll(null, keys);
    }

    public Map<String, E> getAll(Predicate<E> predicate, Iterable<String> keys) {
        Map<String, E> m = _getAll(predicate, keys);
        return m;
    }

    @Override
    public Map<String, E> getAll(Collection<Long> keys) {
        return getAll(null, keys);
    }

    public Map<String, E> getAll(Predicate<E> predicate, Collection<Long> keys) {
        Set<String> keySet = Sets.newHashSet();
        keys.forEach(pk -> keySet.add(String.valueOf(pk)));
        Map<String, E> m = _getAll(predicate, keySet);
        return m;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////// 重写父类的get方法/////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// 私有get方法/////////////////////////////////////////////////////////////////////
    E _makeSure(Predicate<E> predicate, Serializable... pks) {
        Validate.notNull(localCache);
        Validate.isTrue(!ArraysUtils.isEmpty(pks));

        String key = merged2Key(pks);
        E obj = localCache.get(key, (k) -> {
            if (delEntityCache != null) {
                E eWeakReference = delEntityCache.get(k);
                if (eWeakReference != null && eWeakReference.valid()) {
                    return eWeakReference;
                }
            }
            E t = super.get(pks);
            if (t == null) {
                t = newEntityInstance(predicate, pks);
                t.insert();
            }
            return t;
        });
        if (obj == null)
            throw new RuntimeException(String.format("%s:%s", entityType, StringUtils.toString(pks)));
        if (!obj.valid())
            throw new RuntimeException(String.format("%s:%s is not valid!", entityType, StringUtils.toString(pks)));
        return obj;
    }

    E _get(String mergedKey) {
        Validate.notNull(localCache);
        Validate.isTrue(!StringUtils.isEmpty(mergedKey));

        Serializable[] pks = parsed2Pks(mergedKey);
        E obj = localCache.get(mergedKey, (k) -> {
            if (delEntityCache != null) {
                E eWeakReference = delEntityCache.get(k);
                if (eWeakReference != null && eWeakReference.valid()) {
                    return eWeakReference;
                }
            }
            return super.get(pks);
        });
        if (obj == null)
            return null;
        if (!obj.valid())
            return null;
        return obj;
    }

    Map<String, E> _getAll(Predicate<E> predicate, Iterable<String> keys) {
        Validate.notNull(localCache);
        if (CollectionUtils.isEmpty(keys))
            return Collections.emptyMap();

        // getAll不会检查失效，先检查失效缓存
        localCache.cleanUp();
        Map<String, E> m = localCache.getAll(keys, (k) -> {
            Set<String> getKeys = Sets.newHashSet(k);
            Map<String, E> all = new HashMap<>();
            if (delEntityCache != null) {
                for (String k1 : k) {
                    if (delEntityCache.containsKey(k1)) {
                        E eWeakReference = delEntityCache.get(k1);
                        if (eWeakReference != null && eWeakReference.valid()) {
                            all.put(k1, eWeakReference);
                            getKeys.remove(k1);
                        }
                    }
                }
            }
            if (!getKeys.isEmpty()) {
                all.putAll(super.getAll(getKeys));
            }
            return all;
        });
        Map<String, E> result = Maps.newHashMap(m);
        result.values().removeIf(t -> !t.valid() || (predicate != null && !predicate.test(t)));
        return result;
    }

    E _create(Predicate<E> predicate, Serializable... pks) {
        Validate.notNull(localCache);
        Validate.isTrue(!ArraysUtils.isEmpty(pks));

        E obj = newEntityInstance(predicate, pks);

        // TODO 如果不调用insert方法 需要手动调用下afterCreated
        obj.insert();
        localCache.put(obj.getKey(), obj);
        return obj;
    }

    Map<String, E> _createAll(Iterable<String> keys, Predicate<E> predicate) {
        Validate.notNull(localCache);
        if (CollectionUtils.isEmpty(keys))
            return Collections.emptyMap();

        Map<String, E> result = Maps.newHashMap();
        keys.forEach(mergedKey -> {
            Serializable[] pks = parsed2Pks(mergedKey);
            E obj = newEntityInstance(predicate, pks);
            obj.insert();
        });
        localCache.putAll(result);
        return result;
    }

    /**
     * 这里之所以要锁住方法体,是为了避免在并发情况下,多处同时传入相同的keys来获取数据,确保第一次锁就完成创建并添加到缓存,
     * 第二个拿到锁的线程进来可以直接从缓存返回数据不会重复创建
     */
    synchronized Map<String, E> _makeSureAll(Iterable<String> keys, Predicate<E> predicate) {
        Validate.notNull(localCache);
        if (CollectionUtils.isEmpty(keys))
            return Collections.emptyMap();

        Map<String, E> result = Maps.newHashMap();
        Set<String> sourceKeys = Sets.newHashSet(keys);
        // getAll不会检查失效，先检查失效缓存
        localCache.cleanUp();
        Map<String, E> getAll = localCache.getAll(keys, (k) -> {
            Set<String> getKeys = Sets.newHashSet(k);
            Map<String, E> all = new HashMap<>();
            if (delEntityCache != null) {
                for (String k1 : k) {
                    if (delEntityCache.containsKey(k1)) {
                        E eWeakReference = delEntityCache.get(k1);
                        if (eWeakReference != null && eWeakReference.valid()) {
                            all.put(k1, eWeakReference);
                            getKeys.remove(k1);
                        }
                    }
                }
            }
            if (!getKeys.isEmpty()) {
                all.putAll(super.getAll(getKeys));
            }
            return all;
        });
        result.putAll(getAll);
        sourceKeys.removeAll(getAll.keySet());
        if (!sourceKeys.isEmpty()) {
            Map<String, E> createAll = _createAll(sourceKeys, predicate);
            result.putAll(createAll);
        }
        result.values().removeIf(t -> !t.valid() || (predicate != null && !predicate.test(t)));
        return result;
    }

    /////////////////////////////////////////////////////// 私有get方法/////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////// 需要子类重写的方法/////////////////////////////////////////////////////////////////////
    protected long estimatedSize() {
        return localCache == null ? 0L : localCache.estimatedSize();
    }

    // 永久的缓存对象才正确，否则需要谨慎使用,可以用getAllCacheList替代
    protected Map<String, E> asMap() {
        return localCache == null ? Collections.emptyMap() : localCache.asMap();
    }

    public List<E> getAllCacheList() {
        if (localCache != null) {
            localCache.cleanUp();
        }
        Map<String, E> map = new HashMap<>(asMap());
        for (E e : delayCache) {
            if (map.containsKey(e.getKey())) {
                continue;
            }
            map.put(e.getKey(), e);
        }
        delEntityCache.entrySet().forEach(v -> {
            if (!map.containsKey(v.getKey())) {
                map.put(v.getKey(), v.getValue());
            }
        });
        return new ArrayList<>(map.values());
    }

    protected CacheStats stats() {
        return localCache == null ? null : localCache.stats();
    }

    protected Policy<String, E> policy() {
        return localCache == null ? null : localCache.policy();
    }

    private void _remove(String mergedKey) {
        Validate.notNull(localCache);
        Validate.isTrue(!StringUtils.isEmpty(mergedKey));
        localCache.invalidate(mergedKey);
    }

    public void removeCache(Serializable... pks) {
        _remove(merged2Key(pks));
    }

    public void removeAllCache(List<Serializable[]> pksArray) {
        Set<String> keySet = Sets.newHashSet();
        for (Serializable[] pks : pksArray) {
            keySet.add(merged2Key(pks));
        }
        _removeAll(keySet);
    }

    void _removeAll(Iterable<String> keys) {
        Validate.notNull(localCache);
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        localCache.invalidateAll(keys);
    }

    /**
     * 对象被新创建之后,只有缓存和db都没有的时候创建才会调用,new出来的并不会
     */
    protected void afterCreated(E obj) {
        if (PropertyConfig.isDebug()) {
            log.info("create entity and  add to  cache :" + entityType + "[" + obj.getKey() + "]");
        }
    }

    /**
     * 对象被删除前的动作在这里做
     */
    protected void beforeDelete(E obj) {
        if (PropertyConfig.isDebug()) {
            log.info("delete entity and  remove from  cache :" + entityType + "[" + obj.getKey() + "]");
        }
    }

    public void commitSave(E entity) {
        if (entity.options() == MySQLable.Options.INSERT) {
            entity.setFirstCreateTime(new Date());
            afterCreated(entity);
        } else if (entity.options() == MySQLable.Options.DELETE) {
            beforeDelete(entity);
        }
        save(entity);
    }

    public Collection<E> collectionByField(String fieldName, String dbFieldName, Object value) throws Exception {
        List<E> list = new ArrayList<>();
        Field[] declaredFields = entityType.getDeclaredFields();
        for (E e : asMap().values()) {
            boolean find = false;
            for (Field declaredField : declaredFields) {
                if (declaredField.getName().equals(fieldName)) {
                    declaredField.setAccessible(true);
                    Object o = declaredField.get(e);
                    if (Objects.equals(o, value)) {
                        find = true;
                        break;
                    }
                }
            }
            if (find) {
                list.add(e);
            }
        }
        List<E> es = queryWithCondition(String.format("%s=%s", dbFieldName, value));
        for (E e : es) {
            if (list.stream().noneMatch(v -> v.getKey().equals(e.getKey()))) {
                list.add(e);
            }
        }

        return list;
    }

    /////////////////////////////////////////////////////// 需要子类重写的方法/////////////////////////////////////////////////////////////////////
}
