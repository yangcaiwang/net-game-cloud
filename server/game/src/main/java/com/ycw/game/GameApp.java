package com.ycw.game;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNode;
import com.ycw.core.cluster.node.SuperServerNode;

public class GameApp {
    public static void main(String[] args) throws Exception {
        SuperServerNode superServerNode = ServerNode.valueOf(ServerType.GAME_SERVER);
        superServerNode.init();
        superServerNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(superServerNode::stop));
    }
}
