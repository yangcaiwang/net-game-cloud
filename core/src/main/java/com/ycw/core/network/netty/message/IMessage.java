package com.ycw.core.network.netty.message;

import com.google.protobuf.Message;

public interface IMessage {
    int getCmd();

    Message getMessage();

    byte[] getArray();

    long getPlayerId();

    String getServerId();

    /**
     * 构建消息体
     *
     * @param cmd      报文号
     * @param playerId 玩家id
     * @param message  具体proto消息体
     */
    void buildIMessage(int cmd, long playerId, Message message);

    /**
     * 构建消息体
     *
     * @param cmd     报文号
     * @param message 具体proto消息体
     */
    void buildIMessage(int cmd, Message message);

    /**
     * 构建消息体
     *
     * @param cmd 报文号
     */
    void buildIMessage(int cmd);

    /**
     * 构建消息体
     *
     * @param cmd      报文号
     * @param playerId 玩家id
     * @param serverId 服务器id
     * @param bytes    字节数据
     */
    void buildIMessage(int cmd, long playerId, String serverId, byte[] bytes);
}
