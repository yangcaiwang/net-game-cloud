package com.ycw.game;

import com.game.proto.CommonProto;
import com.game.proto.ProtocolProto;
import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNodeComponent;
import com.ycw.core.cluster.node.SuperServerNode;
import com.ycw.core.network.netty.message.IMessage;
import com.ycw.core.network.netty.message.SocketMessage;
import com.ycw.core.network.netty.socket.SocketClient;

public class GameApp {
    public static void main(String[] args) throws Exception {
        SuperServerNode superServerNode = ServerNodeComponent.valueOf(ServerType.GAME_SERVER);
        superServerNode.init();
        superServerNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(superServerNode::stop));
        SocketClient.getInstance().start("127.0.0.1", 7000);

        // 首包
        IMessage iMessage = new SocketMessage();

        iMessage.buildIMessage(ProtocolProto.ProtocolCmd.FIRST_REQ_VALUE, CommonProto.FIRST_REQ.newBuilder().setPlayerId(1L).setServerId("game-1001").build().toByteArray());
        SocketClient.getInstance().channelFuture.channel().writeAndFlush(iMessage);

        // 测试
        IMessage testMessage = new SocketMessage();
        testMessage.buildIMessage(ProtocolProto.ProtocolCmd.HEART_BEAT_REQ_VALUE, CommonProto.MiniItem.newBuilder().setResId(1).setNum(100).build().toByteArray());

//        SocketClient.getInstance().channelFuture.channel().writeAndFlush(iMessage);
    }
}
