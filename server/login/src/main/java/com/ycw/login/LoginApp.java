package com.ycw.login;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNodeComponent;
import com.ycw.core.cluster.node.SuperServerNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginApp {
    public static void main(String[] args) {

        SuperServerNode superServerNode = ServerNodeComponent.valueOf(ServerType.LOGIN_SERVER);
        superServerNode.init();
        superServerNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(superServerNode::stop));
        SpringApplication.run(LoginApp.class, args);
    }
}
