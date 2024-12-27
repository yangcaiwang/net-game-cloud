
package com.ycw.core.internal.db.entity;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.util.SnowflakeIdWorker;

/**
 * <分布式id生成器工具类>
 * <p>
 * ps: 雪花算法实现
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class IdentityCreator {

    /**
     * 服務器类型
     */
    public static ServerType SERVER_TYPE = null;

    /**
     * 创建分布式唯一id
     */
    public static long create() {
        SnowflakeIdWorker idWorker = SnowflakeIdWorker.valueOf();
        return idWorker.nextId();
    }
}
