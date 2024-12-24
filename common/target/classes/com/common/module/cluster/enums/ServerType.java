package com.common.module.cluster.enums;

public enum ServerType {
    ALL_SERVER(0, "all-server", ""),
    GATE_SERVER(1, "gate-server", ""),
    LOGIN_SERVER(2, "login-server", ""),
    GAME_SERVER(3, "game-server", ""),
    CHAT_SERVER(4, "chat-server", ""),
    WEB_SERVER(5, "web-server", ""),
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