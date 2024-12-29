package com.ycw.core.cluster.enums;

/**
 * <客户端显示服务器状态枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum ServerShowState {
    NONE(0), // 非游戏服显示状态
    WHITE(1), //白名单(控制外部玩家能否进入服务器)
    NEW(2), //新服
    HOT(3), //火爆
    BUSY(4), //拥挤
    FREE(5), //空闲
    GRAY(6), //灰度
    MAINTAIN(7), //维护中
    ;
    public final int state;

    ServerShowState(int state) {
        this.state = state;
    }

    public static boolean isWhiteList(int state) {
        return state == WHITE.state;
    }
}