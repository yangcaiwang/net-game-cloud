package com.ycw.core.network.netty.enums;

public enum OffLineCmd {
    NORMAL(-1), // 正常断开，可以重连
    KIT_PEOPLE(-98), // 顶号
    KIT_OUT(-99), // 踢到登录去（网络断开)
    ;
    public final int value;
    OffLineCmd(int value) {
        this.value = value;
    }
}
