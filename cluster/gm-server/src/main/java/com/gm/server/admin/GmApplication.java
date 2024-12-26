package com.gm.server.admin;

import com.common.module.cluster.component.ServerComponent;
import com.common.module.cluster.component.ServerSuperComponent;
import com.common.module.cluster.enums.ServerType;
import com.gm.server.common.utils.ssl.RSAUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动程序
 *
 * @author gamer
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.gm.server.admin.mapper", "com.gm.server.generator.mapper", "com.gm.server.quartz.mapper", "com.gm.server.system.mapper"})
@ComponentScan(basePackages = {"com.gm.server.admin", "com.gm.server.common", "com.gm.server.framework", "com.gm.server.generator", "com.gm.server.quartz", "com.gm.server.system"})
public class GmApplication {
    public static void main(String[] args) {
        SpringApplication.run(GmApplication.class, args);
        ServerSuperComponent gmServerComponent = ServerComponent.valueOf(ServerType.GM_SERVER);
        gmServerComponent.init();
        gmServerComponent.start();
        RSAUtils.load();
    }
}