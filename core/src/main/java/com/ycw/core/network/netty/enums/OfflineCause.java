package com.ycw.core.network.netty.enums;

public enum OfflineCause {
    /**
     * 未知原因
     */
    UNKNOWN,

    /**
     * 手动自行关闭
     */
    MANUAL,

    /**
     * 非法操作,编解码异常
     */
    ILLEGAL,

    /**
     * 超时未注册
     */
    UNREGISTER_TIMEOUT,

    /**
     * 异常.io异常
     */
    EXCEPTION,

    /**
     * 心跳超时
     */
    HEARTBEAT_TIMEOUT,

    /**
     * 顶号
     */
    REPLACE,

    /**
     * 停服
     */
    STOP_SERVER,
    /**
     * 被踢下线
     */
    KIT_OUT,

    /**
     * 玩家数据异常
     */
    ROLE_ERROR,

    /**
     * 禁止登录
     */
    FORBID,
    //
    ;
}
