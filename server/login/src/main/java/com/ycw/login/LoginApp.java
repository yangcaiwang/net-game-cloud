package com.ycw.login;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNode;
import com.ycw.core.cluster.node.ServerSuperNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginApp {
    public static void main(String[] args) {

        ServerSuperNode serverSuperNode = ServerNode.valueOf(ServerType.LOGIN_SERVER);
        serverSuperNode.init();
        serverSuperNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(serverSuperNode::stop));
        SpringApplication.run(LoginApp.class, args);
    }
}
