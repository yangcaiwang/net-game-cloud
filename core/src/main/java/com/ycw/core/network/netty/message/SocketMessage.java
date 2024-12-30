package com.ycw.core.network.netty.message;

import java.io.Serializable;

public class SocketMessage implements IMessage, Serializable {
    // 指令
    private int cmd;
    // message消息体转换为的字节数组
    private byte[] bytes;
    // 客户端私有私有密钥，链接建立时服务器创建并发送给客户端
    private int privateKey;
    // 玩家id
    private long playerId;
    // 玩家所属服务器id
    private String serverId;

    @Override
    public void buildIMessage(int cmd) {
        this.cmd = cmd;
    }

    @Override
    public void buildIMessage(int cmd, byte[] bytes) {
        this.cmd = cmd;
        this.bytes = bytes;
    }

    @Override
    public void buildIMessage(int cmd, byte[] bytes, long playerId) {
        this.cmd = cmd;
        this.bytes = bytes;
        this.playerId = playerId;
    }

    @Override
    public void buildIMessage(int cmd, byte[] bytes, long playerId, String serverId) {
        this.cmd = cmd;
        this.bytes = bytes;
        this.playerId = playerId;
        this.serverId = serverId;
    }

    public int getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(int privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    @Override
    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
