package com.ycw.game;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNodeComponent;
import com.ycw.core.cluster.node.SuperServerNode;
import com.ycw.core.network.netty.socket.SocketClient;

public class GameApp {
    public static void main(String[] args) throws Exception {
        SuperServerNode superServerNode = ServerNodeComponent.valueOf(ServerType.GAME_SERVER);
        superServerNode.init();
        superServerNode.start();
        SocketClient.getInstance().start("127.0.0.1",7000);
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(superServerNode::stop));
    }
}
