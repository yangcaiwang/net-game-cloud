package com.gm.server.common.constant;

public class NettyConst {
    // TCP 服务
    public static final int NET_TCP = 1;
    // WEBSOCKET 服务
    public static final int NET_WEBSOCKET = 2;
    // HTTP 服务
    public static final int NET_WEB_HTTP = 3;
    // TCP & websocket 服务
    public static final int TCP_AND_WEBSOCKET = 0;

    // tcp 或者 websocket的channel类型
    public static final int TCP_CHANNEL = 1;
    public static final int WEBSOCKET_CHANNEL = 2;
}
