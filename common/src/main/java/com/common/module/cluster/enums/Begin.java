package com.common.module.cluster.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * <网络中间件开启枚举类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum Begin {
    DATABASE("db"),
    GRPC_C("grpc"),
    GRPC_S("grpc"),
    NETTY("netty"),
    JETTY("jetty"),
    ;
    public final String key;

    Begin(String key) {
        this.key = key;
    }

    public static Begin getInstance(Begin begin) {
        for (Begin itemTypeEnum : Begin.values()) {
            if (itemTypeEnum.equals(begin)) {
                return itemTypeEnum;
            }
        }

        return null;
    }

    public boolean isBegin(ServerType serverType) {
        List<ServerType> serverTypes = new ArrayList<>();
        switch (this) {
            case DATABASE:
            case GRPC_S:
                serverTypes.add(ServerType.GAME_SERVER);
                serverTypes.add(ServerType.BATTLE_SERVER);
                break;
            case GRPC_C:
            case NETTY:
                serverTypes.add(ServerType.GATE_SERVER);
                break;
            case JETTY:
                serverTypes.add(ServerType.BATTLE_SERVER);
                serverTypes.add(ServerType.CHAT_SERVER);
                serverTypes.add(ServerType.GATE_SERVER);
                serverTypes.add(ServerType.GAME_SERVER);
                serverTypes.add(ServerType.GM_SERVER);
                serverTypes.add(ServerType.LOGIN_SERVER);
                break;
            default:
        }

        return !serverTypes.isEmpty() && serverTypes.contains(serverType);
    }
}
