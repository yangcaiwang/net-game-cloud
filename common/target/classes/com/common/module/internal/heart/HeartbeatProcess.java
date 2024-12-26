package com.common.module.internal.heart;

/**
 * <心跳机制处理器接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface HeartbeatProcess {

    void sent();

    void monitor();

    boolean isTimeOut(long lastHeartBeatTime);

    void showdown();
}
