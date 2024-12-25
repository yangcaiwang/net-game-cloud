package com.common.module.cluster.enums;

/**
 * <服务器类型枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum ServerType {
    ALL_SERVER(0, "all-server", ""),
    CHAT_SERVER(1, "chat-server", ""),
    GAME_SERVER(2, "game-server", ""),
    GATE_SERVER(3, "gate-server", ""),
    GM_SERVER(4, "gm-server", ""),
    LOGIN_SERVER(5, "login-server", ""),
    ;

    private final int value;
    private final String name;
    private String serverId;

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    ServerType(int value, String name, String serverId) {
        this.value = value;
        this.name = name;
    }
}