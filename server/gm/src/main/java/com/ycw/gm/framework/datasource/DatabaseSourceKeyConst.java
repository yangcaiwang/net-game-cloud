package com.ycw.gm.framework.datasource;

public class DatabaseSourceKeyConst {
    public static String getGameKey(Long keyId) {
        return "game_" + keyId;
    }

    public static String getLogKey(Long keyId) {
        return "log_" + keyId;
    }
}
