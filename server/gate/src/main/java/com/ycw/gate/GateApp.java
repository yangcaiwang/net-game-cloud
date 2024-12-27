package com.ycw.gate;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNode;
import com.ycw.core.cluster.node.SuperServerNode;

public class GateApp {

    public static void main(String[] args) {
        SuperServerNode superServerNode = ServerNode.valueOf(ServerType.GATE_SERVER);
        superServerNode.init();
        superServerNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(superServerNode::stop));
    }
}
