package com.common.module.cluster.component;

import com.common.module.cluster.constant.ClusterConstant;
import com.common.module.cluster.enums.ServerType;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.base.BaseConfigUtil;
import com.common.module.internal.db.entity.IdentityCreator;
import com.common.module.internal.db.entity.Repositories;
import com.common.module.internal.loader.Scanner;
import com.common.module.network.netty.message.MessageProcess;
import com.common.module.util.PrintManager;
import com.common.module.util.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * <服务器组件抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractServerComponent implements ServerSuperComponent {

    public static final Logger log = LoggerFactory.getLogger(AbstractServerComponent.class);
    protected ServerType serverType;

    protected Map<String, Object> clusterMap = new HashMap<>();

    protected abstract void startRedission();

    protected abstract void startRegister();

    protected abstract void startDatabase();

    protected abstract void startGrpcServer();

    protected abstract void startJettyServer();

    protected abstract void startNettyServer();

    @Override
    public ServerType serverType() {
        return serverType;
    }

    @Override
    public void init() {
        try {
            // (1)初始化静态配置
            String serverPath = PropertyConfig.getPrefixPath() + "/" + ClusterConstant.CLUSTER_PREFIX + "/" + serverType().getName() + ClusterConstant.SERVER_PATH;
            PropertyConfig.load(serverPath);
            log.info("======================= [{}] 初始化静态配置完毕 =======================", serverType().getName());

            // (2)初始化日志
            String log4jPath = System.getProperty(ClusterConstant.LOG4J_PATH);
            PropertyConfig.initLog4J2(log4jPath);
            log.info("======================= [{}] 初始化日志完毕 =======================", serverType().getName());

            // (3)初始化集群配置
            String path = PropertyConfig.getPrefixPath() + System.getProperty(ClusterConstant.CLUSTER_PATH);
            clusterMap = PropertyConfig.loadYml(path);
            Map<String, Object> cluster = (Map<String, Object>) clusterMap.get(ClusterConstant.CLUSTER_PREFIX);
            serverType().setServerId((String) cluster.get("serverId"));
            IdentityCreator.SERVER_TYPE = serverType();
            log.info("======================= [{}] 初始化集群配置完毕 =======================", serverType().getName());

            // (4)初始化基础配置
            BaseConfigUtil.getInstance().load(PropertyConfig.getPrefixPath() + System.getProperty(ClusterConstant.JSON_PATH));
            log.info("======================= [{}] 初始化基础配置完毕 =======================", serverType().getName());

            // (5)扫描单例
            Scanner.getInstance().scan("com.common.module", "com." + serverType().getName().replace("-", "."));
            log.info("======================= [{}] 扫描单例完毕 =======================", serverType().getName());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        long begin = System.currentTimeMillis();
        startRedission();
        startRegister();
        startDatabase();
        startGrpcServer();
        startJettyServer();
        startNettyServer();
        long timeUsed = System.currentTimeMillis() - begin;
        PrintManager.showPrint(PropertyConfig.isDebug());
        log.info("======================= [{}] 启动完毕,耗时[{}]秒, pid=[{}] =======================", serverType().getServerId(), (timeUsed / 1000f), SystemUtils.getPID());
    }

    @Override
    public void stop() {
        try {
            int players = MessageProcess.getInstance().kitOutAll();
            if (players != 0) {
                log.info("======================= [{}] kit out [{}] player =======================", serverType().getServerId(), players);
            }
            Repositories.flushAll();
            log.info("======================= [{}] all db data updated =======================", serverType().getServerId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }
}
