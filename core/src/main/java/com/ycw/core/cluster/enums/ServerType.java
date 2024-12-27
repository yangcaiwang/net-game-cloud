package com.ycw.core.cluster.enums;

/**
 * <服务器类型枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum ServerType {
    BATTLE_SERVER(1, "battle"),
    CHAT_SERVER(2, "chat"),
    GAME_SERVER(3, "game"),
    GATE_SERVER(4, "gate"),
    GM_SERVER(5, "gm"),
    LOGIN_SERVER(6, "login"),
    ;

    private final int value;
    private final String name;

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
}