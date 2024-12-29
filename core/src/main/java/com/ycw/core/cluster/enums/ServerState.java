package com.ycw.core.cluster.enums;

/**
 * <内部服务器状态枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum ServerState {
    NORMAL(1), // 正常
    ERROR(2),// 异常
    ;

    public final int state;

    private ServerState(int state) {
        this.state = state;
    }

    public static boolean isNormal(int state) {
        return NORMAL.state == state;
    }

    public static boolean isError(int state) {
        return ERROR.state == state;
    }
}