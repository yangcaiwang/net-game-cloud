package com.gm.server;

import com.common.module.cluster.component.ServerComponent;
import com.common.module.cluster.component.ServerSuperComponent;
import com.common.module.cluster.enums.ServerType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoginServerApplication {

    public static void main(String[] args) {

        ServerSuperComponent gmServerComponent = ServerComponent.valueOf(ServerType.LOGIN_SERVER);
        gmServerComponent.init();
        gmServerComponent.start();
//        SpringApplication.run(GmServerApplication.class, args);
    }
}
