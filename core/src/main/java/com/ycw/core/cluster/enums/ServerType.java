package com.ycw.core.cluster.enums;

/**
 * <服务器类型枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum ServerType {
    GM_SERVER(1, "gm"),
    LOGIN_SERVER(2, "login"),
    GATE_SERVER(3, "gate"),
    GAME_SERVER(4, "game"),
    BATTLE_SERVER(5, "battle"),
    ;

    public final int value;
    public final String name;

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    ServerType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static ServerType getByValue(int value) {
        for (ServerType serverType : ServerType.values()) {
            if (serverType.value == value) {
                return serverType;
            }
        }

        return null;
    }

    public static boolean isLoginServer(int value) {
        return value == LOGIN_SERVER.value;
    }

    public static boolean isGmServer(int value) {
        return value == GM_SERVER.value;
    }

    public static boolean isGateServer(int value) {
        return value == GATE_SERVER.value;
    }

    public static boolean isGameServer(int value) {
        return value == GAME_SERVER.value;
    }

    public static boolean isBattleServer(int value) {
        return value == BATTLE_SERVER.value;
    }
}