package com.ycw.core.network.netty.message;

public interface IMessage {
    int getCmd();

    byte[] getBytes();

    long getPlayerId();

    void setPlayerId(long playerId);

    /**
     * 构建消息体
     *
     * @param cmd 报文号
     */
    void buildIMessage(int cmd);

    /**
     * 构建消息体
     *
     * @param cmd   报文号
     * @param bytes 字节数据
     */
    void buildIMessage(int cmd, byte[] bytes);

    /**
     * 构建消息体
     *
     * @param cmd      报文号
     * @param bytes    字节数据
     * @param playerId 玩家id
     */
    void buildIMessage(int cmd, byte[] bytes, long playerId);
}
