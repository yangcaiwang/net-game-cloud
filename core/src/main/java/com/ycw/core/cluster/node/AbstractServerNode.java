package com.ycw.core.cluster.node;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.ServerYmlTemplate;
import com.ycw.core.internal.base.BaseConfigUtil;
import com.ycw.core.internal.db.entity.Repositories;
import com.ycw.core.internal.loader.Scanner;
import com.ycw.core.network.netty.message.MessageProcess;
import com.ycw.core.util.PrintManager;
import com.ycw.core.util.SystemUtils;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.Reader;

/**
 * <服务器组件抽象类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public abstract class AbstractServerNode implements SuperServerNode {

    public static final Logger log = LoggerFactory.getLogger(AbstractServerNode.class);
    protected ServerYmlTemplate serverYmlTemplate;

    protected ServerType serverType;

    protected abstract void startRedission();

    protected abstract void startRegister();

    protected abstract void startDatabase();

    public abstract void startGrpcClient();

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
            // (1)初始化服务器配置
            this.serverYmlTemplate = PropertyConfig.loadYml(PropertyConfig.getAbsolutePath("server.yml", serverType), ServerYmlTemplate.class);
            log.info("======================= [{}] 初始化服务器配置完毕 =======================", getServerId());

            // (2)初始化游戏配置
            PropertyConfig.load(PropertyConfig.getAbsolutePath("server.properties", serverType));
            log.info("======================= [{}] 初始化游戏配置完毕 =======================", getServerId());

            // (3)初始化日志
            PropertyConfig.initLog4J2(PropertyConfig.getAbsolutePath("log4j2.xml", serverType));
            log.info("======================= [{}] 初始化日志完毕 =======================", getServerId());

            // (4)初始化基础配置
            BaseConfigUtil.getInstance().load(PropertyConfig.getAbsolutePath("json/", serverType), getGroupId() + "." + serverType.getName());
            log.info("======================= [{}] 初始化基础配置完毕 =======================", getServerId());

            // (5)扫描单例
            Scanner.getInstance().scan(getGroupId() + ".core", getGroupId() + "." + serverType.getName());
            log.info("======================= [{}] 扫描单例完毕 =======================", getServerId());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void start() {
        try {
            long begin = System.currentTimeMillis();
            startRedission();
            startRegister();
            startDatabase();
            startGrpcClient();
            startGrpcServer();
            startJettyServer();
            startNettyServer();
            long timeUsed = System.currentTimeMillis() - begin;
            PrintManager.showPrint(PropertyConfig.isDebug());
            log.info("======================= [{}] 启动完毕,耗时[{}]秒, pid=[{}] =======================", getServerId(), (timeUsed / 1000f), SystemUtils.getPid());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void stop() {
        try {
            int players = MessageProcess.getInstance().kitOutAll();
            if (players != 0) {
                log.info("======================= [{}] kit out [{}] player =======================", getServerId(), players);
            }
            Repositories.flushAll();
            log.info("======================= [{}] all db data updated =======================", getServerId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取pom依赖的组id
     */
    protected String getGroupId() {
        try {
            Reader reader = new FileReader("pom.xml");
            MavenXpp3Reader xpp3Reader = new MavenXpp3Reader();
            MavenProject project = new MavenProject(xpp3Reader.read(reader));
            return project.getGroupId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取服务器id
     */
    public String getServerId() {
        return serverYmlTemplate.getNode() == null ? "" : serverYmlTemplate.getNode().getServerId();
    }

    protected void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }
}
