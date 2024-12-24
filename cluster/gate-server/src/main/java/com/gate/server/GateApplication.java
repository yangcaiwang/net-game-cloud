package com.gate.server;

import com.common.module.cluster.component.ServerComponent;
import com.common.module.cluster.component.ServerSuperComponent;
import com.common.module.cluster.enums.ServerType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GateApplication {

    public static void main(String[] args) throws IOException {
        ServerSuperComponent gateServerComponent = ServerComponent.valueOf(ServerType.GATE_SERVER);
        gateServerComponent.init();
        gateServerComponent.start();
        // 停服钩子
//        Runtime.getRuntime().addShutdownHook(new Thread(gateComponent::stop));
    }
}
