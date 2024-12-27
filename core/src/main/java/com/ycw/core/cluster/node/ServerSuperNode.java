package com.ycw.core.cluster.node;

import com.ycw.core.cluster.enums.ServerType;

/**
 * <服务器组件接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface ServerSuperNode {

    ServerType serverType();

    void init();

    void start();

    void stop();
}
