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
    CHAT_SERVER(2, "chat"),
    LOGIN_SERVER(3, "login"),
    GATE_SERVER(4, "gate"),
    GAME_SERVER(5, "game"),
    BATTLE_SERVER(6, "battle"),
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