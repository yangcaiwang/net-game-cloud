package com.common.module.cluster.component;

import com.common.module.cluster.enums.ServerType;

/**
 * <服务器组件接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface ServerSuperComponent {

    ServerType serverType();

    void init();

    void start();

    void stop();
}
