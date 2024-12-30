package com.ycw.core.network.netty.message;

import com.google.protobuf.Message;
import com.ycw.core.util.SerializationUtils;

import java.io.Serializable;

public class ProtoMessage implements IMessage, Serializable {
    // 指令
    private int cmd;
    // protobuf结构体
    private Message message;
    // 字节数组
    private byte[] array;
    // 玩家id
    private long playerId;
    // 玩家所属服务器id
    private String serverId;
    // 客户端私有私有密钥，链接建立时服务器创建并发送给客户端
    private int privateKey;

    @Override
    public void buildIMessage(int cmd, long playerId, Message message) {
        this.cmd = cmd;
        this.message = message;
        this.playerId = playerId;
        this.array = SerializationUtils.toByteArrayByH2(this);
    }

    @Override
    public void buildIMessage(int cmd, Message message) {
        this.cmd = cmd;
        this.message = message;
        this.array = SerializationUtils.toByteArrayByH2(this);
    }

    @Override
    public void buildIMessage(int cmd) {
        this.cmd = cmd;
    }

    @Override
    public void buildIMessage(int cmd, long playerId, String serverId, byte[] bytes) {
        this.cmd = cmd;
        this.playerId = playerId;
        this.serverId = serverId;
        this.array = bytes;
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

    @Override
    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public byte[] getArray() {
        return array;
    }

    public void setArray(byte[] array) {
        this.array = array;
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
