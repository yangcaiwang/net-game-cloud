package com.common.module.internal.db.repository;

import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DelayCachedRepository;

import java.io.Serializable;

/**
 * <游戏业务数据仓库抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class CachedRepository<E extends DBEntity> extends DelayCachedRepository<E> {

    /**
     * 根据单一int主键获取数据,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    public E getFromIntKey(int pk) {
        return super.get(pk);
    }

    /**
     * 根据单一long主键获取数据,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    public E getFromLongKey(long pk) {
        return super.get(pk);
    }

    /**
     * 根据单一String主键获取数据,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    public E getFromStringKey(String pk) {
        return super.get(pk);
    }

    /**
     * 根据拼接联合主键的值之后的字符串获取数据,会优先从缓存获取,缓存没有就db获取,db没有返回null,db有加入缓存并返回
     */
    public E getFromMergedStringKey(String mergedKey) {
        Serializable[] pks = parsed2Pks(mergedKey);
        return super.get(pks);
    }
}
