package com.ycw.core.cluster.enums;

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
    JETTY,
    NETTY,
    GRPC_CLI,
    GRPC_SER,
    REDISSION,
    DB_GAME,
    DB_LOG,
    ;

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
            case DB_GAME:
            case DB_LOG:
                serverTypes.add(ServerType.GAME_SERVER);
                break;
            case GRPC_SER:
                serverTypes.add(ServerType.CHAT_SERVER);
                serverTypes.add(ServerType.GAME_SERVER);
                serverTypes.add(ServerType.BATTLE_SERVER);
                break;
            case NETTY:
            case GRPC_CLI:
                serverTypes.add(ServerType.GATE_SERVER);
                break;
            case JETTY:
            case REDISSION:
                serverTypes.add(ServerType.BATTLE_SERVER);
                serverTypes.add(ServerType.CHAT_SERVER);
                serverTypes.add(ServerType.GATE_SERVER);
                serverTypes.add(ServerType.GAME_SERVER);
                serverTypes.add(ServerType.GM_SERVER);
                serverTypes.add(ServerType.LOGIN_SERVER);
                break;
            default:
        }

        return serverTypes.contains(serverType);
    }
}
