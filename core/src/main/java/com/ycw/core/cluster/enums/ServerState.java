package com.ycw.core.cluster.enums;

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
    WHITE(1, false), //白名单可设置进入
    NEW(2, true), //新服
    HOT(3, true), //火爆
    BUSY(4, true), //拥挤
    FREE(5, true), //空闲
    GRAY(6, false), //灰度
    MAINTAIN(7, true), //维护中
    NORMAL(8, true), // 正常
    ERROR(9, true),// 异常
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
        return map.getOrDefault(state, WHITE);
    }

    public static boolean checkShowState(int state) {
        ServerState currServerState = valueOf(state);
        return currServerState.show;
    }

    public static boolean checkNeedWhiteList(int state) {
        return state == WHITE.state;
    }

    public static boolean isMaintain(int state) {
        return state != HOT.state && state != NEW.state;
    }

}