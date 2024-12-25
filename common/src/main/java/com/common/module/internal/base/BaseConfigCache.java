package com.common.module.internal.base;

import com.common.module.cluster.enums.ServerType;
import com.common.module.internal.base.annotation.BaseConfig;
import com.common.module.internal.base.annotation.JsonToBaseConfig;
import com.common.module.internal.base.config.ConstantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * <基础配置数据通用缓存类>
 * <p>
 * ps: 所有解析的配置类都放入这里面 手动生成get set方法
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
@BaseConfig
public class BaseConfigCache {
    private static final Logger log = LoggerFactory.getLogger(BaseConfigCache.class);

    // 常亮
    @JsonToBaseConfig(fileName = "Constant.json", serverType = ServerType.GAME_SERVER)
    private static Map<Integer, ConstantConfig> constantConfig;

    public static Map<Integer, ConstantConfig> getConstantConfig() {
        return constantConfig;
    }

    public static void setConstantConfig(Map<Integer, ConstantConfig> constantConfig) {
        BaseConfigCache.constantConfig = constantConfig;
    }
}
