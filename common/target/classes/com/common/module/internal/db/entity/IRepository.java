
package com.common.module.internal.db.entity;

import com.common.module.internal.db.Mysql;
import com.common.module.internal.db.dao.DaoImpl;
import com.common.module.internal.db.dao.Query;
import com.common.module.util.ArraysUtils;
import com.common.module.util.CollectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <数据仓库超类>
 * <p>
 * ps: 雪花算法实现
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface IRepository<T extends DBEntity> {

    /**
     * 拼接sql条件语句
     *
     * @param options 条件数据,如果value是引用类型,需要自行处理toString
     *                </p>
     *                参考 @see {@link DBEntityUtils} 的getValue(entity,field)
     * @param joiner  连接符, and 或者 or
     */
    default String options(Map<String, Object> options, String joiner) {

        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (Entry<String, Object> entry : options.entrySet()) {
            String column_name = entry.getKey();
            Object value = entry.getValue();
            builder.append("`");
            builder.append(column_name);
            builder.append("`");
            builder.append("=");
            builder.append("'");
            builder.append(value);
            builder.append("'");
            if (i < options.size() - 1)
                builder.append(" ").append(joiner).append(" ");
            i++;
        }
        return builder.toString();
    }

    /**
     * 获取实体类型
     */
    Class<T> entityType();

    /**
     * 数据表
     */
    Set<String> tables();

    /**
     * 根据主键数组,解析成字符串
     */
    default String merged2Key(Serializable... pks) {

        return DBEntityUtils.mergedKey(pks);
    }

    /**
     * 根据字符串，解析主键数组
     */
    default Serializable[] parsed2Pks(String mergedKey) {

        return DBEntityUtils.parsed2Pks(entityType(), mergedKey);
    }

    /**
     * 构造一个实体
     */
    default T newEntityInstance(Predicate<T> predicate, Serializable... pks) {
        return DBEntityUtils.newEntityInstance(predicate, entityType(), pks);
    }

    /**
     * 从db.table中查询一部分数据 select *from table limit begin,end
     */
    default List<T> limitList(long begin, long end) {
        Class<T> entityType = entityType();
        if (DBEntityUtils.needRoll(entityType) || DBEntityUtils.tableNum(entityType) > 1)
            throw new RuntimeException(entityType.getName());
        String aliasName = Mysql.getInstance().aliasName(entityType);
        String tableName = DBEntityUtils.simpleTableName(entityType);
        return DaoImpl.getInstance().queryLimit(entityType(), aliasName, tableName, begin, end);
    }

    /**
     * 从db.table中查询一部分数据 select *from table limit num
     */
    default List<T> limitList(int limit) {
        return limitList(0, limit);
    }

    /**
     * 根据自定义sql语句查询
     */
    default List<Map<String, Object>> query(String sql) {

        return DaoImpl.getInstance().query(entityType(), sql);
    }

    /**
     * 根据自定义sql语句查询
     */
    default void query(String sql, Query query) {

        DaoImpl.getInstance().query(entityType(), sql, query);
    }

    /**
     * 根据自定义条件语句查询
     */
    default List<T> queryWithCondition(String condition) {

        return DaoImpl.getInstance().queryWithCondition(entityType(), condition);
    }

    /**
     * 插入一条数据到数据库并且返回得到的自增ID
     */
    default Long returnGeneratedKeys(T entity) {

        return DaoImpl.getInstance().returnGeneratedKeys(entity);
    }

    /**
     * 插入数据库
     */
    default Boolean insert(Collection<T> c) {

        return DaoImpl.getInstance().insert(c, entityType());
    }

    /**
     * 插入数据库，主键重复就执行更新
     */
    default Boolean insertOrUpdate(Collection<T> c) {

        return DaoImpl.getInstance().insertOrUpdate(c, entityType());
    }

    /**
     * 如果不存在就插入
     */
    default Boolean insertIfNotExists(Collection<T> c) {

        return DaoImpl.getInstance().insertIfNotExists(c, entityType());
    }

    /**
     * 插入如果已存在就替换
     */
    default Boolean replace(Collection<T> c) {

        return DaoImpl.getInstance().replace(c, entityType());
    }

    /**
     * 更新
     */
    default Boolean update(Collection<T> c) {

        return DaoImpl.getInstance().update(c, entityType());
    }

    default Boolean insert(T... objs) {

        return insert(Lists.newArrayList(objs));
    }

    default Boolean insertOrUpdate(T... objs) {

        return insertOrUpdate(Lists.newArrayList(objs));
    }

    default Boolean insertIfNotExists(T... objs) {

        return insertIfNotExists(Lists.newArrayList(objs));
    }

    default Boolean replace(T... objs) {

        return replace(Lists.newArrayList(objs));
    }

    default Boolean update(T... objs) {

        return update(Lists.newArrayList(objs));
    }

    /**
     * 直接通过主键的值获取实体,如果使用了缓存,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    default T get(Serializable... pks) {
        return DaoImpl.getInstance().query(entityType(), pks);
    }

    /**
     * 直接通过一批主键(联合主键数字,每个字段都必须是可序列化的)的值获取一批实体
     * 如果使用了缓存,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    default Map<String, T> getAll(Serializable[]... pksArray) {
        List<Serializable[]> pksList = Lists.newArrayList(pksArray);
        return DaoImpl.getInstance().queryAll(entityType(), pksList);
    }

    /**
     * 直接通过一批主键(联合主键数字,每个字段都必须是可序列化的)的值获取一批实体
     * 如果使用了缓存,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    default Map<String, T> getAll(List<Serializable[]> pksList) {
        return DaoImpl.getInstance().queryAll(entityType(), pksList);
    }

    /**
     * 直接通过一批主键(必须是唯一整数型(byte,short,int,long),不包含浮点和Decmal),都统一转成long,获取一批实体
     * 如果使用了缓存,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    default Map<String, T> getAll(Collection<Long> keys) {
        return DaoImpl.getInstance().queryAll(entityType(), keys);
    }

    /**
     * 直接通过一批主键(联合主键拼接后组成的字符串)获取一批实体
     * 如果使用了缓存,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    default Map<String, T> getAll(Iterable<String> keys) {
        List<Serializable[]> pksList = Lists.newArrayList();
        List<Long> longs = Lists.newArrayList();
        keys.iterator().forEachRemaining(mergedKey -> {
            Serializable[] pks = parsed2Pks(mergedKey);
            pksList.add(pks);
            if (pks.length == 1 && pks[0] instanceof Number) {
                longs.add(((Number) pks[0]).longValue());
            }
        });
        if (!CollectionUtils.isEmpty(longs))
            return DaoImpl.getInstance().queryAll(entityType(), longs);
        else
            return DaoImpl.getInstance().queryAll(entityType(), pksList);
    }

    /**
     * list 转 map
     */
    default Map<String, T> toMap(List<T> list) {
        return list.stream().collect(Collectors.toMap(T::getKey, v -> v));
    }

    /**
     * 直接从db加载所有数据
     */
    default List<T> listAll() {
        long dataCount = dataCount();
        if (dataCount > 10000) {
            String tab = DBEntityUtils.simpleTableName(entityType());
            LoggerFactory.getLogger(entityType()).warn("表[{}].dataCount=[{}],请分批查询", tab, dataCount);
        }
        return DaoImpl.getInstance().queryAll(entityType());
    }

    /**
     * 直接从db加载数据,这个使用的是 and 查询
     */
    default List<T> list(Map<String, Object> options) {

        return DaoImpl.getInstance().queryWithCondition(entityType(), options(options, "and"));
    }

    /**
     * 直接从db加载数据,,这个使用的是 or 模糊查询
     */
    default List<T> listBlurry(Map<String, Object> options) {

        return DaoImpl.getInstance().queryWithCondition(entityType(), options(options, "or"));
    }

    /**
     * 根据条件直接从db查询数据,
     */
    default List<T> list(String condition) {

        return DaoImpl.getInstance().queryWithCondition(entityType(), condition);
    }

    /**
     * 根据条件直接从db查询数据,
     */
    default List<T> list(String field, Object value) {

        Map<String, Object> options = Maps.newHashMap();
        options.put(field, value);
        return list(options);
    }

    /**
     * 根据条件直接从db查询数据,确保该字段符合条件的只有一条数据,通常只有这个字段名表示主键或者唯一索引才可以这么用
     */
    default T get(String field, Object value) {

        List<T> list = list(field, value);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    /**
     * mysql.table数据条数
     */
    default long dataCount() {

        String tableName = DBEntityUtils.simpleTableName(entityType());
        String sql = String.format("SELECT COUNT(*) FROM `%s`", tableName);
        AtomicLong atomicLong = new AtomicLong(0);
        query(sql, rs -> atomicLong.set(rs.getLong(1)));
        return atomicLong.get();
    }

    default boolean deleteNow(String... pks) {
        return false;
    }

    /**
     * 删除表里主键为keys的那一条数据
     */
    default Boolean delete(Serializable... pks) {

        return DaoImpl.getInstance().delete(entityType(), pks);
    }

    /**
     * 删除表里主键为keys的那一条数据
     */
    default Boolean deleteByKeys(String... pks) {

        return DaoImpl.getInstance().deleteByKeys(entityType(), pks);
    }

    /**
     * 批量删除
     */
    default Boolean delete(T... objs) {

        return delete(Lists.newArrayList(objs));
    }

    /**
     * 批量删除
     */
    default Boolean delete(Collection<T> c) {

        return DaoImpl.getInstance().delete(c, entityType());
    }

    /**
     * 删除表里满足条件condition的数据
     */
    default Boolean delete(String condition) {

        return DaoImpl.getInstance().delete(entityType(), condition);
    }

    /**
     * 创建新对象主键(仅仅对单表并且唯一long(18)主键有效)
     */
    long makePk();

    /**
     * 重置内存中主键的自增值(仅仅对唯一long(18)主键有效),只能在清空表的操作后才可以调用,否则会造成主键冲突
     */
    void resetPk();

    default long maxColumn(String column) throws Exception {
        try {
            AtomicLong atomicLong = new AtomicLong();
            query(String.format("SELECT MAX(`%s`) FROM `%s`", column, DBEntityUtils.simpleTableName(entityType())), rs -> atomicLong.set(rs.getLong(1)));
            return atomicLong.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + DBEntityUtils.simpleTableName(entityType()) + "." + column, e);
        }
    }

    default long minColumn(String column) throws Exception {
        try {
            AtomicLong atomicLong = new AtomicLong();
            query(String.format("SELECT MIN(`%s`) FROM `%s`", column, DBEntityUtils.simpleTableName(entityType())), rs -> atomicLong.set(rs.getLong(1)));
            return atomicLong.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + DBEntityUtils.simpleTableName(entityType()) + "." + column, e);
        }
    }

    default long avgColumn(String column) throws Exception {
        try {
            AtomicLong atomicLong = new AtomicLong();
            query(String.format("SELECT AVG(`%s`) FROM `%s`", column, DBEntityUtils.simpleTableName(entityType())), rs -> atomicLong.set(rs.getLong(1)));
            return atomicLong.get();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + DBEntityUtils.simpleTableName(entityType()) + "." + column, e);
        }
    }

    default T makeSure() {
        T obj = newEntityInstance(null, makePk());
        insert(obj);
        return obj;
    }

    default T makeSure(Predicate<T> predicate) {
        T obj = newEntityInstance(predicate, makePk());
        insert(obj);
        return obj;
    }

    default T makeSure(Serializable... pks) {
        return makeSure(null, pks);
    }

    default T makeSure(Predicate<T> predicate, Serializable... pks) {
        T obj = get(pks);
        if (obj != null) {
            return obj;
        }
        obj = newEntityInstance(predicate, pks);
        insert(obj);
        return obj;
    }

    default Map<String, T> makeSureAll(Serializable[]... pksArray) {
        return makeSureAll(null, pksArray);
    }

    default Map<String, T> makeSureAll(Predicate<T> predicate, Serializable[]... pksArray) {
        Map<String, T> map = getAll(pksArray);
        List<Serializable[]> list = Lists.newArrayList(pksArray);
        for (int i = list.size(); --i >= 0; ) {
            String mergedKey = DBEntityUtils.merged(list.get(i));
            if (map.containsKey(mergedKey)) {
                list.remove(i);
            }
        }
        if (!CollectionUtils.isEmpty(list)) {
            Set<T> set = Sets.newHashSet();
            for (Serializable[] pks : list) {
                T obj = newEntityInstance(predicate, pks);
                set.add(obj);
                map.put(obj.getKey(), obj);
            }
            insert(set);
        }
        return map;
    }

    default Map<String, T> makeSureAll(List<Serializable[]> pksList) {
        return makeSureAll(null, pksList);
    }

    default Map<String, T> makeSureAll(Predicate<T> predicate, List<Serializable[]> pksList) {
        return makeSureAll(predicate, ArraysUtils.toArray(pksList, Serializable[].class));
    }

    default Map<String, T> makeSureAll(Predicate<T> predicate, Iterable<String> keys) {
        List<Serializable[]> pksList = Lists.newArrayList();
        List<Long> longs = Lists.newArrayList();
        keys.iterator().forEachRemaining(mergedKey -> {
            Serializable[] pks = parsed2Pks(mergedKey);
            pksList.add(pks);
            if (pks.length == 1 && pks[0] instanceof Number) {
                longs.add(((Number) pks[0]).longValue());
            }
        });
        if (!CollectionUtils.isEmpty(longs))
            return makeSureAll(predicate, longs);
        else
            return makeSureAll(predicate, pksList);
    }

    default Map<String, T> makeSureAll(Iterable<String> keys) {
        return makeSureAll(null, keys);
    }

    default Map<String, T> makeSureAll(Predicate<T> predicate, Collection<Long> keys) {
        Map<String, T> map = getAll(keys);
        List<Long> list = Lists.newArrayList(keys);
        for (int i = list.size(); --i >= 0; ) {
            String mergedKey = String.valueOf(list.get(i));
            if (map.containsKey(mergedKey)) {
                list.remove(i);
            }
        }
        if (!CollectionUtils.isEmpty(list)) {
            Set<T> set = Sets.newHashSet();
            for (Long pk : list) {
                T obj = newEntityInstance(predicate, pk);
                set.add(obj);
                map.put(obj.getKey(), obj);
            }
            insert(set);
        }
        return map;
    }

    default Map<String, T> makeSureAll(Collection<Long> keys) {
        return makeSureAll(null, keys);
    }

    default Map<String, T> makeSureAll(Predicate<T> predicate, int num) {
        Map<String, T> map = Maps.newHashMap();
        while (map.size() < num) {
            T obj = newEntityInstance(predicate, makePk());
            map.put(obj.getKey(), obj);
        }
        insert(map.values());
        return map;
    }

    default Map<String, T> makeSureAll(int num) {
        return makeSureAll(null, num);
    }

    /**
     * 是否需要加入仓库组
     */
    default boolean needCheckJoinGroup() {
        return true;
    }

    default void serverClose() {
    }
}
