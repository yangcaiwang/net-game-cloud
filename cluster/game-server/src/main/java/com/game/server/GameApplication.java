package com.game.server;

import com.common.module.cluster.component.ServerComponent;
import com.common.module.cluster.component.ServerSuperComponent;
import com.common.module.cluster.enums.ServerType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GameApplication {
    public static void main(String[] args) throws Exception {
        ServerSuperComponent gameServerComponent = ServerComponent.valueOf(ServerType.GAME_SERVER);
        gameServerComponent.init();
        gameServerComponent.start();
        // 停服钩子
//        Runtime.getRuntime().addShutdownHook(new Thread(gateComponent::stop));
    }
}
