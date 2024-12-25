package com.common.module.cluster.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * <服务器状态枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum ServerState {

    UNKNOWN(0, false), //无状态，白名单可设置进入
    NEW(1, true), //新服
    HOT(2, true), //火爆
    BUSY(3, true), //拥挤
    FREE(4, true), //空闲
    GRAY(5, false), //灰度
    MAINTAIN(6, true), //维护中
    GIVEUP(7, false), //废弃
    NOTOPEN(8, false),//未开放
    ;

    public final int state;
    public final boolean show;

    private ServerState(int state, boolean show) {
        this.state = state;
        this.show = show;
    }

    private static Map<Integer, ServerState> map = new HashMap<>();

    static {
        for (ServerState s : values()) {
            map.put(s.state, s);
        }
    }

    public static ServerState valueOf(int state) {
        return map.getOrDefault(state, UNKNOWN);
    }

    public static boolean checkShowState(int state) {
        ServerState currServerState = valueOf(state);
        return currServerState.show;
    }

    public static boolean checkNeedWhiteList(int state) {
        return state == UNKNOWN.state;
    }
}