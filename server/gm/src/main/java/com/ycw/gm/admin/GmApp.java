package com.ycw.gm.admin;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.node.ServerNode;
import com.ycw.core.cluster.node.SuperServerNode;
import com.ycw.gm.common.utils.ssl.RSAUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.ycw.gm.admin.mapper", "com.ycw.gm.generator.mapper", "com.ycw.gm.quartz.mapper", "com.ycw.gm.system.mapper"})
@ComponentScan(basePackages = {"com.ycw.gm.admin", "com.ycw.gm.common", "com.ycw.gm.framework", "com.ycw.gm.generator", "com.ycw.gm.quartz", "com.ycw.gm.system"})
public class GmApp {
    public static void main(String[] args) {
        SpringApplication.run(GmApp.class, args);
        SuperServerNode superServerNode = ServerNode.valueOf(ServerType.GM_SERVER);
        superServerNode.init();
        superServerNode.start();
        // 停服钩子
        Runtime.getRuntime().addShutdownHook(new Thread(superServerNode::stop));
        RSAUtils.load();
    }
}