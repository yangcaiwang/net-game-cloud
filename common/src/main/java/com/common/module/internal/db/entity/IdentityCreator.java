
package com.common.module.internal.db.entity;

import com.common.module.cluster.enums.ServerType;
import com.common.module.util.SnowflakeIdWorker;

/**
 * 分布式id生成器 :雪花算法实现
 *
 * @author yangcaiwang
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
