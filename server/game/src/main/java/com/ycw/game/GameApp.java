package com.ycw.game;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNode;
import com.ycw.core.cluster.node.ServerSuperNode;

public class GameApp {
    public static void main(String[] args) throws Exception {
        ServerSuperNode serverSuperNode = ServerNode.valueOf(ServerType.GAME_SERVER);
        serverSuperNode.init();
        serverSuperNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(serverSuperNode::stop));
    }
}
